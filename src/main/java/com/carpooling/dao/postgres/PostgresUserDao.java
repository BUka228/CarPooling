package com.carpooling.dao.postgres;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.utils.HibernateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.UUID;


import org.hibernate.HibernateException;

@Slf4j
@AllArgsConstructor
public class PostgresUserDao implements UserDao {

    private final SessionFactory sessionFactory;

    @Override
    public String createUser(User user) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.info("User created successfully: {}", user.getId());
            return user.getId().toString();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error creating user: {}", e.getMessage());
            throw new DataAccessException("Error creating user", e) {};
        }
    }

    @Override
    public Optional<User> getUserById(String id) throws DataAccessException {
        try (Session session = sessionFactory.openSession()) {
            UUID uuid = UUID.fromString(id);
            User user = session.get(User.class, uuid);
            if (user != null) {
                log.info("User found: {}", id);
            } else {
                log.warn("User not found: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            log.error("Error reading user: {}", e.getMessage());
            throw new DataAccessException("Error reading user", e) {};
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e) {};
        }
    }

    @Override
    public void updateUser(User userRecord) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(userRecord);
            transaction.commit();
            log.info("User updated successfully: {}", userRecord.getId());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error updating user: {}", e.getMessage());
            throw new DataAccessException("Error updating user", e) {};
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UUID uuid = UUID.fromString(id);
            User user = session.get(User.class, uuid);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                log.info("User deleted successfully: {}", id);
            } else {
                log.warn("User not found for deletion: {}", id);
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error deleting user: {}", e.getMessage());
            throw new DataAccessException("Error deleting user", e) {};
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e) {};
        }
    }
}
