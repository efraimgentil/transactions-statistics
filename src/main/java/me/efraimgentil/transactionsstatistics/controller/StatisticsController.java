package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.domain.Statistic;
import me.efraimgentil.transactionsstatistics.service.TransactionService;
import me.efraimgentil.transactionsstatistics.service.statistic.TransactionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/statistics" , produces = MediaType.APPLICATION_JSON_VALUE )
public class StatisticsController {

    private final TransactionService service;

    @Autowired
    public StatisticsController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public Statistic getStatistics(){
        return service.getStatistic();
    }
}
