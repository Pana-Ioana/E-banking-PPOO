package ro.ase.projects.ppoo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String accountId;
    private String ownerName;
    private double balance;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String id, String owner, double balance) {
        this.accountId = id;
        this.ownerName = owner;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    @Override
    public String toString() {
        return accountId + " | " + ownerName + " | " + balance;
    }


}
