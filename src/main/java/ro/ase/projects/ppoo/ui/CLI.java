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
            System.out.println("Date incarcate din fisierele TEXT.");
        } catch (DataStorageException e) {
            System.out.println("Fisierele TEXT nu au putut fi incarcate. Se incearca BINAR...");
        }

        if (!loaded) {
            try {
                binaryStorage.load(collections);
                System.out.println("Date incarcate din fisierele BINARE.");
            } catch (DataStorageException e) {
                System.out.println("Eroare critica: nu s-au putut incarca datele.");
            }
        }
    }

    private void runMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENIU E-BANKING ===");
            System.out.println("1. Overview");
            System.out.println("2. Lista conturi");
            System.out.println("3. Detalii cont");
            System.out.println("4. Adauga tranzactie");
            System.out.println("5. Statistici");
            System.out.println("0. Iesire si salvare");
            System.out.print("Alege: ");

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> printOverview();
                case "2" -> printAccounts();
                case "3" -> printAccountDetails();
                case "4" -> addTransaction();
                case "5" -> printStatistics();
                case "0" -> running = false;
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private void printOverview() {
        System.out.println("\nConturi: " + collections.getAccounts().size());
        System.out.println("Tranzactii: " + collections.getAllTransactions().size());
    }

    private void printAccounts() {
        System.out.println();
        for (Account acc : collections.getAccounts()) {
            System.out.println(acc.getAccountId() + " | " +
                    acc.getOwnerName() + " | " + acc.getBalance());
        }
    }

    private void printAccountDetails() {
        System.out.print("ID cont: ");
        String id = scanner.nextLine();

        Account acc = collections.findAccountById(id);
        if (acc == null) {
            System.out.println("Cont inexistent.");
            return;
        }

        System.out.println("\n" + acc.getAccountId() + " | " + acc.getOwnerName());

        List<Transaction> list = acc.getTransactions();
        if (list.isEmpty()) {
            System.out.println("(fara tranzactii)");
            return;
        }

        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    private void addTransaction() {
        System.out.print("ID cont: ");
        String id = scanner.nextLine();

        Account acc = collections.findAccountById(id);
        if (acc == null) {
            System.out.println("Cont inexistent.");
            return;
        }

        System.out.print("Tip (DEPOSIT/WITHDRAW/PAYMENT/TRANSFER): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("Suma: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Data (yyyy-MM-dd) sau ENTER pentru acum: ");
        String d = scanner.nextLine();

        LocalDate date = d.isEmpty()
                ? LocalDate.now()
                : LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String tid = "T" + System.currentTimeMillis();

        Transaction t = new Transaction(tid, id, type, amount, date);
        collections.addTransaction(id, t);

        System.out.println("Tranzactie adaugata.");
    }

    private void printStatistics() {
        statistics.stats(collections);

        System.out.println("\n=== STATISTICI ===");
        System.out.println("Total deposit: " + statistics.getTotalsPerType()[0]);
        System.out.println("Total withdraw: " + statistics.getTotalsPerType()[1]);
        System.out.println("Total payment: " + statistics.getTotalsPerType()[2]);
        System.out.println("Total transfer: " + statistics.getTotalsPerType()[3]);

        System.out.println("Media: " + statistics.getAverageAmount());
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
            System.out.println("Eroare la salvare: " + e.getMessage());
        }
    }
}
