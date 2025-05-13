package inheritance.common;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import jakarta.persistence.criteria.CriteriaQuery; // Для findAll

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class GenericDao<T, ID extends Serializable> { // ID должен быть Serializable
    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    public GenericDao(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T save(T entity) {
        getCurrentSession().persist(entity);
        return entity;
    }

    public T update(T entity) {
        return getCurrentSession().merge(entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getCurrentSession().get(entityClass, id));
    }

    public List<T> findAll() {
        // Используем Criteria API для более безопасного способа получения имени сущности
        CriteriaQuery<T> cq = getCurrentSession().getCriteriaBuilder().createQuery(entityClass);
        cq.from(entityClass);
        return getCurrentSession().createQuery(cq).getResultList();
    }

    public void delete(T entity) {
        // Для удаления объект должен быть в состоянии persistent
        if (getCurrentSession().contains(entity)) {
            getCurrentSession().remove(entity);
        } else {
            // Если detached, сначала merge (чтобы прикрепить к сессии), потом remove
            // Или можно загрузить по ID и удалить
            T managedEntity = getCurrentSession().merge(entity);
            getCurrentSession().remove(managedEntity);
        }
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}