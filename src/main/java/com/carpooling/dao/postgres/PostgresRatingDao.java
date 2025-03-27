package com.carpooling.dao.postgres;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.*;

import static com.carpooling.constants.Constants.FIND_RATING_BY_USER_AND_TRIP_HQL;

@Slf4j
public class PostgresRatingDao extends AbstractPostgresDao<Rating, UUID> implements RatingDao {

    public PostgresRatingDao(SessionFactory sessionFactory) {
        super(sessionFactory, Rating.class);
    }

    @Override
    public String createRating(Rating rating) throws DataAccessException {
        persistEntity(rating);
        if (rating.getId() == null) {
            throw new DataAccessException("Failed to generate ID for rating");
        }
        return rating.getId().toString();
    }

    @Override
    public Optional<Rating> getRatingById(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "rating id");
        // Простой get, т.к. Rating обычно не требует join fetch по умолчанию
        return findEntityById(uuid);
    }

    @Override
    public void updateRating(Rating rating) throws DataAccessException {
        mergeEntity(rating);
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "rating id");
        deleteEntityById(uuid);
    }

    @Override
    public List<Rating> findRatingsByTripId(String tripId) throws DataAccessException {
        log.debug("Finding ratings for trip ID (using Trip entity collection): {}", tripId);
        UUID tripUUID = parseUUID(tripId, "trip ID");
        try {
            Session session = getCurrentSession();
            // Используем getReference, чтобы не грузить всю поездку, если она не нужна
            Trip trip = session.getReference(Trip.class, tripUUID);
            Hibernate.initialize(trip.getRatings()); // Инициализируем коллекцию
            log.info("Retrieved {} ratings for trip ID {}", trip.getRatings().size(), tripId);
            return new ArrayList<>(trip.getRatings()); // Возвращаем копию
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.warn("Trip not found for ID: {}, cannot retrieve ratings.", tripId);
            return Collections.emptyList();
        } catch (PersistenceException e) {
            log.error("Error finding ratings for trip {}: {}", tripId, e.getMessage());
            throw new DataAccessException("Error finding ratings by trip", e);
        }
    }

    @Override
    public Optional<Rating> findRatingByUserAndTrip(String userId, String tripId) throws DataAccessException {
        log.debug("Finding rating for user ID {} and trip ID {}", userId, tripId);
        UUID userUUID = parseUUID(userId, "user ID");
        UUID tripUUID = parseUUID(tripId, "trip ID");
        try {
            Session session = getCurrentSession();
            Query<Rating> query = session.createQuery(FIND_RATING_BY_USER_AND_TRIP_HQL, Rating.class);
            query.setParameter("userId", userUUID);
            query.setParameter("tripId", tripUUID);
            return query.uniqueResultOptional();
        } catch (PersistenceException e) {
            log.error("Error finding rating by user {} and trip {}: {}", userId, tripId, e.getMessage());
            throw new DataAccessException("Error finding rating by user and trip", e);
        }
    }
}