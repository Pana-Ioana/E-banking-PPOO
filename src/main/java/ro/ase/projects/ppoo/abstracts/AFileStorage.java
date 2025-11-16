package ro.ase.projects.ppoo.abstracts;

import ro.ase.projects.ppoo.exceptions.DataStorageException;
import ro.ase.projects.ppoo.interfaces.DataStorage;
import ro.ase.projects.ppoo.services.Collections;

public abstract class AFileStorage implements DataStorage {
    protected String accountsFilePath;
    protected String transactionsFilePath;
    protected String reportFilePath;

    public AFileStorage(String accountsFilePath, String transactionsFilePath, String reportFilePath) {
        this.accountsFilePath = accountsFilePath;
        this.transactionsFilePath = transactionsFilePath;
        this.reportFilePath = reportFilePath;
    }

    @Override
    public void load(Collections collections) throws DataStorageException {
        try {
            readAccounts(collections);
            readTransactions(collections);
        } catch (Exception e) {
            throw new DataStorageException("Error loading data from files", e);
        }
    }

    @Override
    public void save(Collections collections) throws DataStorageException {
        try {
            writeAccounts(collections);
            writeTransactions(collections);
        } catch (Exception e) {
            throw new DataStorageException("Error saving data to files", e);
        }
    }

    @Override
    public void generateReport(Collections collections) throws DataStorageException {
        try {
            writeReport(collections);
        } catch (Exception e) {
            throw new DataStorageException("Error generating report", e);
        }
    }

    protected abstract void readAccounts(Collections collections) throws Exception;

    protected abstract void readTransactions(Collections collections) throws Exception;

    protected abstract void writeAccounts(Collections collections) throws Exception;

    protected abstract void writeTransactions(Collections collections) throws Exception;

    protected abstract void writeReport(Collections collections) throws Exception;

}
