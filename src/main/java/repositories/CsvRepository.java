package repositories;

import exceptions.RepositoryException;
import providers.IDataProvider;

import java.util.List;

public class CsvRepository<T> extends GenericRepository<T> {
    public CsvRepository(IDataProvider<T> provider) {
        super(provider);
    }

}
