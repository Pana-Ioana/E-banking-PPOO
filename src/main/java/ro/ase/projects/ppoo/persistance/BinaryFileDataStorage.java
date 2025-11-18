package ro.ase.projects.ppoo.persistance;

import ro.ase.projects.ppoo.abstracts.AFileStorage;
import ro.ase.projects.ppoo.model.Account;
import ro.ase.projects.ppoo.model.Transaction;
import ro.ase.projects.ppoo.services.Collections;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinaryFileDataStorage extends AFileStorage {

    public BinaryFileDataStorage(String accountsFilePath,
                                 String transactionsFilePath,
                                 String reportFilePath) {
        super(accountsFilePath, transactionsFilePath, reportFilePath);
    }
    @Override
    protected void readAccounts(Collections collections) throws Exception {
        File file = new File(accountsFilePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> raw = (List<?>) obj;

                collections.getAccounts().clear();
                collections.getTransactionsByAccount().clear();

                for (Object o : raw) {
                    if (o instanceof Account acc) {
                        collections.addAccount(acc);

                        for (Transaction t : acc.getTransactions()) {
                            collections.addTransaction(acc.getAccountId(), t);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void readTransactions(Collections collections) throws Exception {
        File file = new File(transactionsFilePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> raw = (List<?>) obj;
                System.out.println("Read " + raw.size() + " transactions from binary file");
            }
        }
    }

    @Override
    protected void writeAccounts(Collections collections) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(accountsFilePath))) {
            oos.writeObject(new ArrayList<>(collections.getAccounts()));
        }
    }

    @Override
    protected void writeTransactions(Collections collections) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(transactionsFilePath))) {
            oos.writeObject(new ArrayList<>(collections.getAllTransactions()));
        }
    }
    @Override
    protected void writeReport(Collections collections) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath))) {

            List<Account> accounts = collections.getAccounts();
            List<Transaction> allTransactions = collections.getAllTransactions();

            bw.write("=== BINARY E-BANKING REPORT ===");
            bw.newLine();
            bw.newLine();

            bw.write("Number of accounts: " + accounts.size());
            bw.newLine();
            bw.write("Number of transactions: " + allTransactions.size());
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
