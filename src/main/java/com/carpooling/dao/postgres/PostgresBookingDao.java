package com.carpooling.dao.postgres;


import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
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
public class PostgresBookingDao implements BookingDao {
    
    private final SessionFactory sessionFactory;

    @Override
    public String createBooking(Booking booking) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(booking);
            transaction.commit();
            log.info("Booking created successfully: {}", booking.getId());
            return booking.getId().toString();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error creating booking: {}", e.getMessage());
            throw new DataAccessException("Error creating booking", e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(String id) throws DataAccessException {
        try (Session session = sessionFactory.openSession()) {
            UUID uuid = UUID.fromString(id);
            Booking booking = session.get(Booking.class, uuid);
            if (booking != null) {
                log.info("Booking found: {}", id);
            } else {
                log.warn("Booking not found: {}", id);
            }
            return Optional.ofNullable(booking);
        } catch (HibernateException e) {
            log.error("Error reading booking: {}", e.getMessage());
            throw new DataAccessException("Error reading booking", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }

    @Override
    public void updateBooking(Booking booking) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(booking);
            transaction.commit();
            log.info("Booking updated successfully: {}", booking.getId());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error updating booking: {}", e.getMessage());
            throw new DataAccessException("Error updating booking", e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UUID uuid = UUID.fromString(id);
            Booking booking = session.get(Booking.class, uuid);
            if (booking != null) {
                session.remove(booking);
                transaction.commit();
                log.info("Booking deleted successfully: {}", id);
            } else {
                log.warn("Booking not found for deletion: {}", id);
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error deleting booking: {}", e.getMessage());
            throw new DataAccessException("Error deleting booking", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }
}