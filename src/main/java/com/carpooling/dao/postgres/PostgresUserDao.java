package com.carpooling.dao.postgres;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PostgresUserDao extends AbstractPostgresDao<User, UUID> implements UserDao {

    public PostgresUserDao(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public String createUser(User user) throws DataAccessException {
        persistEntity(user);
        // ID должен быть доступен после persist
        if (user.getId() == null) {
            // Этого не должно произойти, если генерация ID работает
            log.error("User ID was null after persist! User: {}", user);
            throw new DataAccessException("Failed to generate ID for user");
        }
        return user.getId().toString();
    }

    @Override
    public Optional<User> getUserById(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "user id");
        return findEntityById(uuid);
    }

    @Override
    public void updateUser(User user) throws DataAccessException {
        mergeEntity(user);
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "user id");
        deleteEntityById(uuid);
    }

    @Override
    public Optional<User> findByEmail(String email) throws DataAccessException, OperationNotSupportedException {
        log.debug("Finding user by email (using NaturalId): {}", email);
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        try {
            // NaturalId API работает с текущей сессией
            return getCurrentSession().byNaturalId(User.class)
                    .using("email", email)
                    .loadOptional();
        } catch (UnsupportedOperationException e) { // NaturalId может кинуть это
            log.error("@NaturalId lookup failed for email {}: {}", email, e.getMessage());
            throw new OperationNotSupportedException("NaturalId lookup not configured correctly for User email.", e);
        } catch (PersistenceException e) { // Общая ошибка Hibernate/JPA
            log.error("Error finding user by email {}: {}", email, e.getMessage());
            throw new DataAccessException("Error finding user by email", e);
        }
    }
}
