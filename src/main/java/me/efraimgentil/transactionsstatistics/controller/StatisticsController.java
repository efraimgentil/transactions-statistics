package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/statistic" , produces = MediaType.APPLICATION_JSON_VALUE )
public class StatisticsController {

    private final TransactionStatistics storage;

    @Autowired
    public StatisticsController(TransactionStatistics storage) {
        this.storage = storage;
    }

    @GetMapping
    public Statistic getStatistics(){
        return storage.getStatistic();
    }
}
