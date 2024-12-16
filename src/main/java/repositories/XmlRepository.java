package repositories;

import providers.IDataProvider;

public class XmlRepository<T> extends GenericRepository<T> {
    public XmlRepository(IDataProvider<T> provider) {
        super(provider);
    }

}
