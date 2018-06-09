package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/transactions" , produces = MediaType.APPLICATION_JSON_VALUE , consumes =  MediaType.APPLICATION_JSON_VALUE )
public class TransactionController {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity addTransaction(@RequestBody Transaction transaction){
        service.addTransactionStatistic(transaction);
        return ResponseEntity.created(null).build();
    }

}
