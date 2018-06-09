package me.efraimgentil.transactionsstatistics.service.statistic;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionCacheService implements TransactionStatistics {

    private Statistic statistic;
    private final AtomicReference<TreeMap<Long, List<Transaction>>> timedCache;
    private final TaskScheduler scheduler;
    private final Integer rangeInSeconds;

    @Autowired
    public TransactionCacheService(TaskScheduler scheduler , @Value("${statistics.rangeInSeconds}") Integer rangeInSeconds) {
        timedCache = new AtomicReference<>(new TreeMap<>());
        this.scheduler = scheduler;
        this.rangeInSeconds = rangeInSeconds;
    }

    @Async
    @Override
    public void addToStatistic(Transaction transaction) {
        Long timestamp = transaction.getTimestamp();
        timedCache.getAndUpdate(map -> {
            map.computeIfAbsent(timestamp,  aLong -> new ArrayList<>() );
            map.computeIfPresent(timestamp, (aLong, transactions) -> { transactions.add(transaction); return transactions; } );
            updateStatistic(transaction);
            return map;
        });
    }

    protected long getExpirationTime(Transaction t){
        return Instant.ofEpochMilli(t.getTimestamp()).plusSeconds(rangeInSeconds).toEpochMilli();
    }

    protected void updateStatistic(Transaction t) {
        statistic = getCurrentPartialStatistic().update(t).toStatistic();
    }

    protected void updateStatistic(NavigableMap<Long, List<Transaction>> map) {
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
        Double min;
        double max = 0;
        long count = 0L;

        private PartialStatistic update(Transaction t){
            double amount = t.getAmount();
            if(amount > max){
                max = amount;
            }
            if(min == null || amount < min){
                min = amount;
            }
            sum += t.getAmount();
            count++;
            return this;
        }

        private Statistic toStatistic(){
            double avg = count > 0L ? sum/count : 0L;
            min =  Optional.ofNullable(min).orElse(0.0);
            return new Statistic(sum , avg , max , min , count );
        }
    }
}
