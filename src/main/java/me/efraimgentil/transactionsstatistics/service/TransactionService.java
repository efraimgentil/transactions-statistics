package me.efraimgentil.transactionsstatistics.service;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.exception.OldTransactionException;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionCacheService;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final Integer rangeInSeconds;
    private final TransactionStatistics statistics;

    public TransactionService(@Value("${statistics.rangeInSeconds}") Integer rangeInSeconds , TransactionStatistics transactionStatistics) {
        this.rangeInSeconds = rangeInSeconds;
        this.statistics = transactionStatistics;
    }

    public void addTransactionStatistic(Transaction transaction) {
        if(isInStatisticRange(transaction)){
            statistics.addToStatistic(transaction);
        }else{
            logger.info("Transaction rejected, because is too old {}" , transaction);
            throw new OldTransactionException("Transaction too old to be added to statistic" , transaction);
        }
    }

    public Statistic getStatistic(){
        return statistics.getStatistic();
    }

    /**
     * Verify if the transaction is in the range of the last 60 seconds
     * @param transaction
     * @return
     */
    protected boolean isInStatisticRange(Transaction transaction){
        //Instant uses UTC value
        Instant maxAcceptableInstant = Instant.now().minusSeconds(rangeInSeconds);
        return Instant.ofEpochMilli(transaction.getTimestamp()).isAfter(maxAcceptableInstant);
    }



}
