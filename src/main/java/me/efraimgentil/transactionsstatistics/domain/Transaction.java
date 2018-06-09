package me.efraimgentil.transactionsstatistics.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Double amount;
    private Long timestamp;

    @JsonCreator
    public Transaction(@JsonProperty("amount") Double amount, @JsonProperty("timestamp") Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
