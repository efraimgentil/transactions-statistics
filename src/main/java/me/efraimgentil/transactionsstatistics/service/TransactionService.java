package me.efraimgentil.transactionsstatistics.service;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.exception.OldTransactionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionService {

    private final Integer rangeInSeconds;

    public TransactionService(@Value("${statistics.rangeInSeconds}") Integer rangeInSeconds) {
        this.rangeInSeconds = rangeInSeconds;
    }

    public void addTransactionStatistic(Transaction transaction) {
        if(isInStatisticRange(transaction)){

        }else{
            throw new OldTransactionException("Transaction too old to be added to statistic" , transaction);
        }
    }

    protected boolean isInStatisticRange(Transaction transaction){
        long expirationRange = Instant.now().minusSeconds(rangeInSeconds).toEpochMilli();
        return expirationRange < transaction.getTimestamp();
    }



}
