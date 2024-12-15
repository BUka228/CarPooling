package repositories;

import exceptions.RepositoryException;
import providers.IDataProvider;

import java.util.List;

public class XmlRepository<T> extends GenericRepository<T> {
    public XmlRepository(IDataProvider<T> provider) {
        super(provider);
    }

}
