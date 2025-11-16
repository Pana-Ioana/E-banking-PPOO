package ro.ase.projects.ppoo.interfaces;

import ro.ase.projects.ppoo.exceptions.DataStorageException;
import ro.ase.projects.ppoo.services.Collections;

public interface DataStorage {
    void load(Collections collections) throws DataStorageException;

    void save(Collections collections) throws DataStorageException;

    void generateReport(Collections collections) throws DataStorageException;
}
