package me.efraimgentil.transactionsstatistics.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/transactions" , produces = MediaType.APPLICATION_JSON_VALUE , consumes =  MediaType.APPLICATION_JSON_VALUE )
public class TransactionController {

    @PostMapping
    public ResponseEntity addTransaction(){
        return ResponseEntity.noContent().build();
    }

}
