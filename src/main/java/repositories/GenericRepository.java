package repositories;

import exceptions.RepositoryException;
import providers.IDataProvider;

import java.util.List;

public abstract class GenericRepository<T> {
    protected final IDataProvider<T> provider;

    public GenericRepository(IDataProvider<T> provider) {
        this.provider = provider;
    }

    public void save(T entity) {
        try {
            provider.saveRecord(entity);
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при сохранении объекта", e);
        }
    }

    public List<T> findAll() {
        try {
            return provider.getAllRecords();
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при получении всех объектов", e);
        }
    }

    public T findById(String id) {
        try {
            return provider.getRecordById(id);
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при поиске объекта по ID", e);
        }
    }

    public void delete(T entity) {
        try {
            provider.deleteRecord(entity);
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при удалении объекта", e);
        }
    }
}
