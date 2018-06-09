package me.efraimgentil.transactionsstatistics.domain;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Double amount;
    private Long timestamp;

    public Transaction(Double amount, Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
