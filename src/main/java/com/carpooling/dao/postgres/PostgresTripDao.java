package com.carpooling.dao.postgres;

import com.carpooling.dao.base.TripDao;

import com.carpooling.entities.database.Trip;
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
public class PostgresTripDao implements TripDao {

    private final SessionFactory sessionFactory;

    @Override
    public String createTrip(Trip trip) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trip);
            transaction.commit();
            log.info("Trip created successfully: {}", trip.getId());
            return trip.getId().toString();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error creating trip: {}", e.getMessage());
            throw new DataAccessException("Error creating trip", e);
        }
    }

    @Override
    public Optional<Trip> getTripById(String id) throws DataAccessException {
        try (Session session = sessionFactory.openSession()) {
            UUID uuid = UUID.fromString(id);
            Trip trip = session.get(Trip.class, uuid);
            if (trip != null) {
                log.info("Trip found: {}", id);
            } else {
                log.warn("Trip not found: {}", id);
            }
            return Optional.ofNullable(trip);
        } catch (HibernateException e) {
            log.error("Error reading trip: {}", e.getMessage());
            throw new DataAccessException("Error reading trip", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }

    @Override
    public void updateTrip(Trip trip) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(trip);
            transaction.commit();
            log.info("Trip updated successfully: {}", trip.getId());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error updating trip: {}", e.getMessage());
            throw new DataAccessException("Error updating trip", e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UUID uuid = UUID.fromString(id);
            Trip trip = session.get(Trip.class, uuid);
            if (trip != null) {
                session.remove(trip);
                transaction.commit();
                log.info("Trip deleted successfully: {}", id);
            } else {
                log.warn("Trip not found for deletion: {}", id);
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error deleting trip: {}", e.getMessage());
            throw new DataAccessException("Error deleting trip", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }
}
