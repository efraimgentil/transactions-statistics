package me.efraimgentil.transactionsstatistics.exception;

import me.efraimgentil.transactionsstatistics.domain.Transaction;

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
