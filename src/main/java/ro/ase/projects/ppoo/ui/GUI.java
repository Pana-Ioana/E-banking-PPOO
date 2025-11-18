package ro.ase.projects.ppoo.ui;

import ro.ase.projects.ppoo.exceptions.DataStorageException;
import ro.ase.projects.ppoo.model.Account;
import ro.ase.projects.ppoo.model.Transaction;
import ro.ase.projects.ppoo.persistance.TextFileDataStorage;
import ro.ase.projects.ppoo.services.Collections;
import ro.ase.projects.ppoo.services.Statistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GUI extends JFrame {

    private final Collections collections;
    private final Statistics statistics;
    private final TextFileDataStorage textStorage;

    private JTable accountsTable;
    private JTable transactionsTable;
    private DefaultTableModel accountsModel;
    private DefaultTableModel transactionsModel;

    private JTextField accountIdField;
    private JComboBox<String> typeCombo;
    private JTextField amountField;
    private JTextField dateField;

    private JTextArea statsArea;

    public GUI(Collections collections, Statistics statistics, TextFileDataStorage textStorage) {
        this.collections = collections;
        this.statistics = statistics;
        this.textStorage = textStorage;

        if (this.collections.getAccounts().isEmpty()) {
            try {
                textStorage.load(this.collections);
            } catch (DataStorageException e) {
                System.out.println("GUI - failed to load data: " + e.getMessage());
            }
        }

        setTitle("E-Banking Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Accounts", buildAccountsPanel());
        tabs.add("Add transaction", buildAddTransactionPanel());
        tabs.add("Statistics", buildStatisticsPanel());

        setContentPane(tabs);

        reloadAccountsTable();

        if (!collections.getAccounts().isEmpty()) {
            String firstId = collections.getAccounts().get(0).getAccountId();
            reloadTransactionsTable(firstId);
        }

        refreshStatistics();
    }

    private JPanel buildAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        accountsModel = new DefaultTableModel(
                new String[]{"Account ID", "Owner", "Balance"}, 0
        );
        accountsTable = new JTable(accountsModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        accountsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = accountsTable.getSelectedRow();
                if (row >= 0) {
                    String accountId = (String) accountsModel.getValueAt(row, 0);
                    reloadTransactionsTable(accountId);
                }
            }
        });

        JScrollPane accountsScroll = new JScrollPane(accountsTable);

        transactionsModel = new DefaultTableModel(
                new String[]{"Transaction ID", "Type", "Amount", "Date"}, 0
        );
        transactionsTable = new JTable(transactionsModel);
        JScrollPane transactionsScroll = new JScrollPane(transactionsTable);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                accountsScroll,
                transactionsScroll
        );
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void reloadAccountsTable() {
        accountsModel.setRowCount(0);
        for (Account acc : collections.getAccounts()) {
            accountsModel.addRow(new Object[]{
                    acc.getAccountId(),
                    acc.getOwnerName(),
                    acc.getBalance()
            });
        }
    }

    private void reloadTransactionsTable(String accountId) {
        transactionsModel.setRowCount(0);

        Account acc = collections.findAccountById(accountId);
        if (acc == null) {
            return;
        }

        List<Transaction> list = acc.getTransactions();
        for (Transaction t : list) {
            transactionsModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getType(),
                    t.getAmount(),
                    t.getTimestamp()
            });
        }
    }

    private JPanel buildAddTransactionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        accountIdField = new JTextField(20);
        typeCombo = new JComboBox<>(new String[]{"DEPOSIT", "WITHDRAW", "PAYMENT", "TRANSFER"});
        amountField = new JTextField(10);
        dateField = new JTextField(10);
        dateField.setToolTipText("yyyy-MM-dd (empty = today)");

        JButton addButton = new JButton("Add transaction");

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Account ID:"), c);
        c.gridx = 1;
        panel.add(accountIdField, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Type:"), c);
        c.gridx = 1;
        panel.add(typeCombo, c);

        c.gridx = 0; c.gridy = 2;
        panel.add(new JLabel("Amount:"), c);
        c.gridx = 1;
        panel.add(amountField, c);

        c.gridx = 0; c.gridy = 3;
        panel.add(new JLabel("Date (yyyy-MM-dd):"), c);
        c.gridx = 1;
        panel.add(dateField, c);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        panel.add(addButton, c);

        addButton.addActionListener(e -> handleAddTransaction());

        return panel;
    }

    private void handleAddTransaction() {
        String accountId = accountIdField.getText().trim();
        if (accountId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account ID is required.");
            return;
        }

        Account acc = collections.findAccountById(accountId);
        if (acc == null) {
            JOptionPane.showMessageDialog(this, "Account not found.");
            return;
        }

        String type = (String) typeCombo.getSelectedItem();

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be positive.");
            return;
        }

        String dateStr = dateField.getText().trim();
        LocalDate date;
        if (dateStr.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format (use yyyy-MM-dd).");
                return;
            }
        }

        String transactionId = "T" + System.currentTimeMillis();
        Transaction t = new Transaction(transactionId, accountId, type, amount, date);

        collections.addTransaction(accountId, t);

        reloadAccountsTable();
        reloadTransactionsTable(accountId);

        JOptionPane.showMessageDialog(this, "Transaction added.");
    }

    private JPanel buildStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JButton refreshButton = new JButton("Refresh statistics");
        refreshButton.addActionListener(e -> refreshStatistics());

        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshStatistics() {
        statistics.stats(collections);

        double[] totals = statistics.getTotalsPerType();
        int[][] matrix = statistics.getMonthlyMatrix();

        StringBuilder sb = new StringBuilder();
        sb.append("=== STATISTICS ===\n");
        sb.append("Total DEPOSIT: ").append(totals[0]).append("\n");
        sb.append("Total WITHDRAW: ").append(totals[1]).append("\n");
        sb.append("Total PAYMENT: ").append(totals[2]).append("\n");
        sb.append("Total TRANSFER: ").append(totals[3]).append("\n\n");

        sb.append("Average amount: ").append(statistics.getAverageAmount()).append("\n");
        sb.append("Min amount: ").append(statistics.getMinAmount()).append("\n");
        sb.append("Max amount: ").append(statistics.getMaxAmount()).append("\n\n");

        sb.append("Type x month (transaction count):\n");
        String[] types = {"DEPOSIT", "WITHDRAW", "PAYMENT", "TRANSFER"};
        for (int i = 0; i < matrix.length; i++) {
            sb.append(types[i]).append(": ");
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(matrix[i][j]).append(" ");
            }
            sb.append("\n");
        }

        statsArea.setText(sb.toString());
    }
}
