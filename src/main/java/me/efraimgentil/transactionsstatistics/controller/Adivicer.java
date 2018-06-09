package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.exception.OldTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class Adivicer {

    private static final Logger logger = LoggerFactory.getLogger(Adivicer.class);

    @ExceptionHandler(OldTransactionException.class)
    public ResponseEntity handleError(HttpServletRequest req, OldTransactionException ex) {
        logger.warn("Transaction was rejected for the statistic because is too old. Transaction: {}" , ex.getTransaction());
        return ResponseEntity.noContent().build();
    }
}
