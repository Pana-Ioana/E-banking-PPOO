package ro.ase.projects.ppoo.ui;

import ro.ase.projects.ppoo.exceptions.DataStorageException;
import ro.ase.projects.ppoo.model.Account;
import ro.ase.projects.ppoo.model.Transaction;
import ro.ase.projects.ppoo.persistance.BinaryFileDataStorage;
import ro.ase.projects.ppoo.persistance.TextFileDataStorage;
import ro.ase.projects.ppoo.services.Collections;
import ro.ase.projects.ppoo.services.Statistics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private final Collections collections = new Collections();
    private final Statistics statistics = new Statistics();

    private final TextFileDataStorage textStorage = new TextFileDataStorage(
            "src/main/java/ro/ase/projects/ppoo/files/accounts.txt",
            "src/main/java/ro/ase/projects/ppoo/files/transactions.txt",
            "src/main/java/ro/ase/projects/ppoo/files/report.txt"
    );

    private final BinaryFileDataStorage binaryStorage = new BinaryFileDataStorage(
            "src/main/java/ro/ase/projects/ppoo/files/accounts.bin",
            "src/main/java/ro/ase/projects/ppoo/files/transactions.bin",
            "src/main/java/ro/ase/projects/ppoo/files/report_bin.txt"
    );

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        loadData();
        runMenu();
        saveData();
    }

    private void loadData() {
        boolean loaded = false;

        try {
            textStorage.load(collections);
            loaded = true;
            System.out.println("Data loaded from text files");
        } catch (DataStorageException e) {
            System.out.println("Text file loading error: " + e.getMessage());
        }

        if (!loaded) {
            try {
                binaryStorage.load(collections);
                System.out.println("Data loaded from binary files");
            } catch (DataStorageException e) {
                System.out.println("Error loading from binary files: " + e.getMessage());
            }
        }
    }

    private void runMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== E-BANKING MENU ===");
            System.out.println("1. Overview");
            System.out.println("2. Accounts list");
            System.out.println("3. Account details");
            System.out.println("4. Add transaction");
            System.out.println("5. Statistics");
            System.out.println("0. Exit");
            System.out.print("Enter: ");

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> printOverview();
                case "2" -> printAccounts();
                case "3" -> printAccountDetails();
                case "4" -> addTransaction();
                case "5" -> printStatistics();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void printOverview() {
        System.out.println("\nAccounts: " + collections.getAccounts().size());
        System.out.println("Transactions: " + collections.getAllTransactions().size());
    }

    private void printAccounts() {
        System.out.println();
        for (Account acc : collections.getAccounts()) {
            System.out.println(acc.getAccountId() + " | " +
                    acc.getOwnerName() + " | " + acc.getBalance());
        }
    }

    private void printAccountDetails() {
        System.out.print("Account ID: ");
        String id = scanner.nextLine();

        Account acc = collections.findAccountById(id);
        if (acc == null) {
            System.out.println("Inexistent account");
            return;
        }

        System.out.println("\n" + acc.getAccountId() + " | " + acc.getOwnerName());

        List<Transaction> list = acc.getTransactions();
        if (list.isEmpty()) {
            System.out.println("(without transactions)");
            return;
        }

        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    private void addTransaction() {
        System.out.print("Account ID: ");
        String id = scanner.nextLine();

        Account acc = collections.findAccountById(id);
        if (acc == null) {
            System.out.println("Inexistent account");
            return;
        }

        System.out.print("Type (DEPOSIT/WITHDRAW/PAYMENT/TRANSFER): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Date (yyyy-MM-dd) or ENTER for now: ");
        String d = scanner.nextLine();

        LocalDate date = d.isEmpty()
                ? LocalDate.now()
                : LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String tid = "T" + System.currentTimeMillis();

        Transaction t = new Transaction(tid, id, type, amount, date);
        collections.addTransaction(id, t);

        System.out.println("Added transaction: " + t);
    }

    private void printStatistics() {
        statistics.stats(collections);

        System.out.println("\n=== STATISTICS ===");
        System.out.println("Total deposit: " + statistics.getTotalsPerType()[0]);
        System.out.println("Total withdraw: " + statistics.getTotalsPerType()[1]);
        System.out.println("Total payment: " + statistics.getTotalsPerType()[2]);
        System.out.println("Total transfer: " + statistics.getTotalsPerType()[3]);

        System.out.println("Mean: " + statistics.getAverageAmount());
        System.out.println("Min: " + statistics.getMinAmount());
        System.out.println("Max: " + statistics.getMaxAmount());
    }

    private void saveData() {
        try {
            textStorage.save(collections);
            textStorage.generateReport(collections);

            binaryStorage.save(collections);
            binaryStorage.generateReport(collections);

        } catch (DataStorageException e) {
            System.out.println("Error saving " + e.getMessage());
        }
    }
}
