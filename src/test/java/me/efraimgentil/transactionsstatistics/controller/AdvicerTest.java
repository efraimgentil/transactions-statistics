package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import me.efraimgentil.transactionsstatistics.service.exception.OldTransactionException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class AdvicerTest {

    Adivicer adivicer;

    @Before
    public void setUp(){
        adivicer = new Adivicer();
    }

    @Test
    public void shouldReturnNoContentInTheResponse(){
        ResponseEntity response = adivicer.handleError(null, new OldTransactionException("FAKE", new Transaction(0.0, Instant.now().toEpochMilli())));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
