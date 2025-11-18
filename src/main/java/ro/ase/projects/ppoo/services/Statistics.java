package ro.ase.projects.ppoo.services;

import ro.ase.projects.ppoo.enums.TransactionType;
import ro.ase.projects.ppoo.model.Transaction;

import java.util.Arrays;
import java.util.List;

public class Statistics {
    private final double[] totalPerType = new double[TransactionType.values().length];
    private final int[][] monthlyMatrix = new int[TransactionType.values().length][12];

    private double averageAmount;
    private double maxAmount;
    private double minAmount;

    public void stats(Collections collections) {
        Arrays.fill(totalPerType, 0.0);
        for (int i = 0; i < monthlyMatrix.length; i++) {
            Arrays.fill(monthlyMatrix[i], 0);
        }
        averageAmount = 0.0;
        minAmount = Double.MAX_VALUE;
        maxAmount = Double.MIN_VALUE;

        List<Transaction> all = collections.getAllTransactions();
        if (all.isEmpty()) return;

        double sum = 0;
        maxAmount = Double.MIN_VALUE;
        minAmount = Double.MAX_VALUE;

        for (Transaction t : all) {
            int typeIndex = mapType(t.getType().name());
            totalPerType[typeIndex] += t.getAmount();

            int monthIndex = t.getTimestamp().getMonth().getValue() - 1;
            monthlyMatrix[typeIndex][monthIndex]++;

            sum += t.getAmount();
            if (t.getAmount() > maxAmount) maxAmount = t.getAmount();
            if (t.getAmount() < minAmount) minAmount = t.getAmount();
        }

        averageAmount = sum / all.size();
    }

    private int mapType(String type) {
        return switch (type.toUpperCase()) {
            case "DEPOSIT" -> 0;
            case "WITHDRAW" -> 1;
            case "PAYMENT" -> 2;
            case "TRANSFER" -> 3;
            default -> 0;
        };
    }

    public double[] getTotalsPerType() {
        return totalPerType;
    }

    public int[][] getMonthlyMatrix() {
        return monthlyMatrix;
    }

    public double getAverageAmount() {
        return averageAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public double getMinAmount() {
        return minAmount;
    }
}
