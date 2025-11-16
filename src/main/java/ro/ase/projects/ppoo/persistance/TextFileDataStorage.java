package ro.ase.projects.ppoo.persistance;

import ro.ase.projects.ppoo.abstracts.AFileStorage;
import ro.ase.projects.ppoo.enums.TransactionType;
import ro.ase.projects.ppoo.model.Account;
import ro.ase.projects.ppoo.model.Transaction;
import ro.ase.projects.ppoo.services.Collections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TextFileDataStorage extends AFileStorage {
    public TextFileDataStorage(String accountsFilePath, String transactionsFilePath, String reportFilePath) {
        super(accountsFilePath, transactionsFilePath, reportFilePath);
    }

    @Override
    protected void readAccounts(Collections collections) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(accountsFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;

                String id = parts[0].trim();
                String owner = parts[1].trim();
                double balance = Double.parseDouble(parts[2].trim());

                Account account = new Account(id, owner, balance);
                collections.getAccounts().add(account);
            }
        }
    }

    @Override
    protected void readTransactions(Collections collections) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;

                String transactionId = parts[0].trim();
                String accountId = parts[1].trim();
                String type = parts[2].trim();
                double amount = Double.parseDouble(parts[3].trim());
                LocalDate date = LocalDate.parse(parts[4].trim());

                Transaction transaction =
                        new Transaction(transactionId, accountId, type, amount, date);

                collections.addTransaction(accountId, transaction);
            }
        }
    }

    @Override
    protected void writeAccounts(Collections collections) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(transactionsFilePath))) {
            for (Map.Entry<String, List<Transaction>> entry :
                    collections.getTransactionsByAccount().entrySet()) {

                for (Transaction t : entry.getValue()) {
                    bw.write(t.getTransactionId() + "|" +
                            t.getAccountId() + "|" +
                            t.getType() + "|" +
                            t.getAmount() + "|" +
                            t.getTimestamp());
                    bw.newLine();
                }
            }
        }
    }

    @Override
    protected void writeTransactions(Collections collections) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(transactionsFilePath))) {
            for (Map.Entry<String, List<Transaction>> entry :
                    collections.getTransactionsByAccount().entrySet()) {

                for (Transaction t : entry.getValue()) {
                    bw.write(t.getTransactionId() + "|" +
                            t.getAccountId() + "|" +
                            t.getType() + "|" +
                            t.getAmount() + "|" +
                            t.getTimestamp());
                    bw.newLine();
                }
            }
        }
    }

    @Override
    protected void writeReport(Collections collections) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath))) {

            List<Account> accounts = collections.getAccounts();
            List<Transaction> allTransactions = collections.getAllTransactions();

            bw.write("=== RAPORT E-BANKING ===");
            bw.newLine();
            bw.newLine();

            bw.write("Numar conturi: " + accounts.size());
            bw.newLine();
            bw.write("Numar tranzactii: " + allTransactions.size());
            bw.newLine();
            bw.newLine();

            double totalDeposits = 0;
            double totalWithdraws = 0;
            double totalPayments = 0;
            double totalTransfers = 0;

            for (Transaction t : allTransactions) {
                String type = t.getType().toString().toUpperCase();

                switch (type) {
                    case "DEPOSIT" -> totalDeposits += t.getAmount();
                    case "WITHDRAW" -> totalWithdraws += t.getAmount();
                    case "PAYMENT" -> totalPayments += t.getAmount();
                    case "TRANSFER" -> totalTransfers += t.getAmount();
                }
            }

            bw.write("Total DEPOSITS: " + totalDeposits);
            bw.newLine();
            bw.write("Total WITHDRAWS: " + totalWithdraws);
            bw.newLine();
            bw.write("Total PAYMENTS: " + totalPayments);
            bw.newLine();
            bw.write("Total TRANSFERS: " + totalTransfers);
            bw.newLine();
        }
    }
}
