package com.carpooling.dao.postgres;
import com.carpooling.exceptions.dao.DataAccessException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable; // ID должен быть Serializable
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class AbstractPostgresDao<T, ID extends Serializable> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass; // Класс сущности
    private final String entityName;    // Имя сущности для логов

    protected AbstractPostgresDao(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
        this.entityName = entityClass.getSimpleName(); // Получаем имя класса для логов
    }

    /**
     * Возвращает текущую сессию Hibernate, управляемую извне.
     * @return Текущая сессия.
     * @throws DataAccessException Если сессию не удалось получить.
     */
    protected Session getCurrentSession() throws DataAccessException {
        try {
            return sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            log.error("Could not get current Hibernate session for {}", entityName, e);
            throw new DataAccessException("Could not access current database session", e);
        }
    }

    /**
     * Сохраняет новую сущность.
     * @param entity Сущность для сохранения.
     * @return Сохраненная сущность (может отличаться от исходной, если есть генерация ID).
     * @throws DataAccessException При ошибке сохранения.
     */
    public T persistEntity(T entity) throws DataAccessException {
        log.debug("Persisting new {}", entityName);
        try {
            Session session = getCurrentSession();
            session.persist(entity);
            log.info("{} persisted within current transaction.", entityName);
            return entity;
        } catch (PersistenceException e) {
            log.error("Error persisting {}: {}", entityName, e.getMessage());
            throw new DataAccessException("Error creating " + entityName, e);
        }
    }

    /**
     * Находит сущность по ID.
     * @param id ID сущности.
     * @return Optional с сущностью.
     * @throws DataAccessException При ошибке чтения.
     */
    public Optional<T> findEntityById(ID id) throws DataAccessException {
        log.debug("Looking up {} by id {}", entityName, id);
        if (id == null) {
            log.warn("Attempted to find {} with null ID.", entityName);
            return Optional.empty(); // Возвращаем empty для null ID
        }
        try {
            T entity = getCurrentSession().get(entityClass, id);
            log.debug("{} lookup by id {} result: {}", entityName, id, entity != null);
            return Optional.ofNullable(entity);
        } catch (PersistenceException e) {
            log.error("Error reading {} by id {}: {}", entityName, id, e.getMessage());
            throw new DataAccessException("Error reading " + entityName, e);
        }
    }

    /**
     * Обновляет сущность (или вставляет, если не существует и настроено каскадирование).
     * @param entity Сущность для обновления.
     * @return Обновленная сущность (управляемая сессией).
     * @throws DataAccessException При ошибке обновления.
     */
    public T mergeEntity(T entity) throws DataAccessException {
        log.debug("Merging {}", entityName);
        try {
            Session session = getCurrentSession();
            // merge возвращает управляемую копию
            T mergedEntity = session.merge(entity);
            log.info("{} merged within current transaction.", entityName);
            return mergedEntity;
        } catch (PersistenceException e) {
            log.error("Error merging {}: {}", entityName, e.getMessage());
            throw new DataAccessException("Error updating " + entityName, e);
        }
    }

    /**
     * Помечает сущность для удаления.
     * @param id ID сущности для удаления.
     * @throws DataAccessException При ошибке удаления или если ID невалиден.
     */
    public void deleteEntityById(ID id) throws DataAccessException {
        log.debug("Attempting to delete {} by id {}", entityName, id);
        if (id == null) {
            log.warn("Attempted to delete {} with null ID.", entityName);
            // Не бросаем ошибку, как и раньше
            return;
        }
        try {
            Session session = getCurrentSession();
            // Используем getReference для ленивой загрузки перед удалением
            // Это может быть эффективнее, чем get, если сама сущность не нужна
            T entityRef = session.getReference(entityClass, id);
            session.remove(entityRef);
            log.info("{} (id={}) marked for removal within current transaction.", entityName, id);
        } catch (jakarta.persistence.EntityNotFoundException e) { // getReference кидает это
            log.warn("{} not found for deletion attempt: {}", entityName, id);
            // Не бросаем ошибку
        } catch (PersistenceException e) {
            log.error("Error deleting {} by id {}: {}", entityName, id, e.getMessage());
            throw new DataAccessException("Error deleting " + entityName, e);
        }
    }

    /**
     * Вспомогательный метод для безопасного парсинга UUID из строки.
     * @param id Строка ID.
     * @param idName Имя поля ID для логов.
     * @return Распарсенный UUID.
     * @throws DataAccessException Если формат строки неверный.
     */
    protected UUID parseUUID(String id, String idName) throws DataAccessException {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for {}: {}", idName, id, e);
            throw new DataAccessException("Invalid UUID format for " + idName + ": " + id, e);
        }
    }
}