package me.efraimgentil.transactionsstatistics.exception;

import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


// I did chose to the approach with @ControllerAdivice for logging purposes
//@ResponseStatus(value= HttpStatus.NO_CONTENT)
public class OldTransactionException extends RuntimeException {

    private Transaction transaction;

    public OldTransactionException(String message, Transaction transaction) {
        super(message);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

}
