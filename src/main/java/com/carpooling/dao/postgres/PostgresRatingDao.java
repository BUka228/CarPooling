package com.carpooling.dao.postgres;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.utils.HibernateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import org.hibernate.HibernateException;


@Slf4j
@AllArgsConstructor
public class PostgresRatingDao implements RatingDao {

    private final SessionFactory sessionFactory;

    @Override
    public String createRating(Rating rating) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(rating);
            transaction.commit();
            log.info("Rating created successfully: {}", rating.getId());
            return rating.getId().toString();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error creating rating: {}", e.getMessage());
            throw new DataAccessException("Error creating rating", e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String id) throws DataAccessException {
        try (Session session = sessionFactory.openSession()) {
            UUID uuid = UUID.fromString(id);
            Rating rating = session.get(Rating.class, uuid);
            if (rating != null) {
                log.info("Rating found: {}", id);
            } else {
                log.warn("Rating not found: {}", id);
            }
            return Optional.ofNullable(rating);
        } catch (HibernateException e) {
            log.error("Error reading rating: {}", e.getMessage());
            throw new DataAccessException("Error reading rating", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }

    @Override
    public void updateRating(Rating rating) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(rating);
            transaction.commit();
            log.info("Rating updated successfully: {}", rating.getId());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error updating rating: {}", e.getMessage());
            throw new DataAccessException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UUID uuid = UUID.fromString(id);
            Rating rating = session.get(Rating.class, uuid);
            if (rating != null) {
                session.remove(rating);
                transaction.commit();
                log.info("Rating deleted successfully: {}", id);
            } else {
                log.warn("Rating not found for deletion: {}", id);
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error deleting rating: {}", e.getMessage());
            throw new DataAccessException("Error deleting rating", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }
}
