package me.efraimgentil.transactionsstatistics.service.statistic;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionCacheService implements TransactionStatistics {

    private static final Logger logger = LoggerFactory.getLogger(TransactionCacheService.class);

    private Statistic statistic;
    private final AtomicReference<TreeMap<Long, List<Transaction>>> timedCache;
    private final AtomicReference<Map<Long, ScheduledFuture>> expirationSchedule;
    private final TaskScheduler scheduler;
    private final Integer rangeInSeconds;

    @Autowired
    public TransactionCacheService(TaskScheduler scheduler , @Value("${statistics.rangeInSeconds}") Integer rangeInSeconds) {
        timedCache = new AtomicReference<>(new TreeMap<>());
        expirationSchedule = new AtomicReference<>(new HashMap<>());
        this.statistic = Statistic.empty();
        this.scheduler = scheduler;
        this.rangeInSeconds = rangeInSeconds;
    }

    @Override
    public void addToStatistic(Transaction transaction) {
        Long timestamp = transaction.getTimestamp();
        logger.info("Adding transaction({}) to statistics" , transaction);
        timedCache.getAndUpdate(map -> {
            map.computeIfAbsent(timestamp,  aLong -> new ArrayList<>() );
            map.computeIfPresent(timestamp, (aLong, transactions) -> { transactions.add(transaction); return transactions; } );
            updateCurrentStatistic(transaction);
            scheduleExpirer(transaction);
            return map;
        });
    }

    /**
     * Create a scheduled job to expire the transaction in the given configure range time and
     * redo the calculations for the statistic
     * @param transaction
     */
    protected void scheduleExpirer(Transaction transaction){
        final Long timestamp = transaction.getTimestamp();
        expirationSchedule.getAndUpdate(scheduleMap -> {
            logger.debug("Scheduling expiration for {}(timestamp) to {} seconds in the future" , timestamp , rangeInSeconds);
            scheduleMap.putIfAbsent(timestamp ,
                 scheduler.schedule(() -> removeExpiredValue(timestamp) , getExpirationTime(transaction)) );
            return scheduleMap;
        });
    }

    protected void removeExpiredValue(Long timestamp){
        timedCache.getAndUpdate(map -> {
            TreeMap<Long, List<Transaction>> newMap = new TreeMap<>(map.tailMap(timestamp , false));
            logger.debug("Cleaning old values from instant: {}  At: {}" , Instant.ofEpochMilli(timestamp) , Instant.now());
            logger.debug("Values remaining in the cache" , newMap.values());
            recalculateStatistics(newMap);
            expirationSchedule.updateAndGet(schedulerMap -> { schedulerMap.remove(timestamp); return schedulerMap; });
            return newMap;
        });
    }

    protected Instant getExpirationTime(Transaction t){
        //Instant uses UTC value
        return Instant.ofEpochMilli(t.getTimestamp()).plusSeconds(rangeInSeconds);
    }

    /**
     * Update statistics considering the new transaction and the old statistic values
     */
    protected void updateCurrentStatistic(Transaction t) {
        statistic = getCurrentPartialStatistic().update(t).toStatistic();
    }

    /**
     * Update statistics considering tall
     */
    protected void recalculateStatistics(NavigableMap<Long, List<Transaction>> map) {
        PartialStatistic partial = new PartialStatistic();
        map.entrySet().stream().map(e -> e.getValue()).flatMap(v -> v.stream()).forEach(t ->  partial.update(t));
        statistic = partial.toStatistic();
    }

    @Override
    public Statistic getStatistic() {
        return statistic == null ? Statistic.empty() : statistic;
    }

    private PartialStatistic getCurrentPartialStatistic(){
        PartialStatistic partialStatistic = new PartialStatistic();
        partialStatistic.min = statistic.getMin();
        partialStatistic.max = statistic.getMax();
        partialStatistic.sum = statistic.getSum();
        partialStatistic.count = statistic.getCount();
        return partialStatistic;
    }

    private class PartialStatistic{
        double sum = 0;
        double min = 0;
        double max = 0;
        long count = 0L;

        private PartialStatistic update(Transaction t){
            double amount = t.getAmount();
            count++;
            if(amount > max){
                max = amount;
            }
            if(amount < min || ( count > 0 && min == 0.0)){
                min = amount;
            }
            sum += t.getAmount();
            return this;
        }

        private Statistic toStatistic(){
            double avg = count > 0L ? sum/count : 0L;
            return new Statistic(sum , avg , max , min , count );
        }
    }
}
