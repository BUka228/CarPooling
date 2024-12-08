package repositories;

import java.util.List;

public interface GenericRepository<T> {
    public void saveAll(List<T> entities);
    void save(T entity);
    List<T> findAll();
    void deleteAll();
}
