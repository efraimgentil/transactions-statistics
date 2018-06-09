package me.efraimgentil.transactionsstatistics.service;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.exception.OldTransactionException;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionService {

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
            throw new OldTransactionException("Transaction too old to be added to statistic" , transaction);
        }
    }

    public Statistic getStatistic(){
        return statistics.getStatistic();
    }

    protected boolean isInStatisticRange(Transaction transaction){
        long expirationRange = Instant.now().minusSeconds(rangeInSeconds).toEpochMilli();
        return expirationRange < transaction.getTimestamp();
    }



}
