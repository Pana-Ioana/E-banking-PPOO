package ro.ase.projects.ppoo.services;

import ro.ase.projects.ppoo.model.Account;
import ro.ase.projects.ppoo.model.Transaction;

import java.util.*;

public class Collections {
    List<Account> accounts = new ArrayList<>();
    Map<String, List<Transaction>> transactionsByAccount = new HashMap<>();

    double[] totalsPerType;
    int[][] transactionsMatrix;
    private Calendar transactions;

    public List<Account> getAccounts() {
        return accounts;
    }

    public Map<String, List<Transaction>> getTransactionsByAccount() {
        return transactionsByAccount;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account findAccountById(String accountId) {
        for (Account acc : accounts) {
            if (acc.getAccountId().equals(accountId)) {
                return acc;
            }
        }
        return null;
    }

    public void addTransaction(String accountId, Transaction transaction) {
        List<Transaction> list = transactionsByAccount
                .computeIfAbsent(accountId, k -> new ArrayList<>());
        list.add(transaction);

        Account acc = findAccountById(accountId);
        if (acc != null) {
            acc.getTransactions().add(transaction);
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> all = new ArrayList<>();
        for (List<Transaction> list : transactionsByAccount.values()) {
            all.addAll(list);
        }
        return all;
    }
}
