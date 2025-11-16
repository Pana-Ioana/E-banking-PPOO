package ro.ase.projects.ppoo.model;

import ro.ase.projects.ppoo.enums.TransactionType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String transactionId;
    private String accountId;
    private double amount;
    private TransactionType type;
    private LocalDate timestamp;

    public Transaction(String id, String accountId, String type, double amount, LocalDate timestamp) {
        this.transactionId = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = TransactionType.valueOf(type.toUpperCase());
        this.timestamp = timestamp;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return transactionId + " | " + accountId + " | " + type + " | " + amount + " | " + timestamp;
    }

}
