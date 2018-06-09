package me.efraimgentil.transactionsstatistics.service.exception;

import me.efraimgentil.transactionsstatistics.domain.Transaction;


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
