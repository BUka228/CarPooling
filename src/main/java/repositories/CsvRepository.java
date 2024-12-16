package repositories;

import providers.IDataProvider;

public class CsvRepository<T> extends GenericRepository<T> {
    public CsvRepository(IDataProvider<T> provider) {
        super(provider);
    }

}
