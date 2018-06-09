package me.efraimgentil.transactionsstatistics.service.statistic;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.springframework.stereotype.Service;

public interface TransactionStatistics {

    public void addToStatistic(Transaction transaction);

    public Statistic getStatistic();

}
