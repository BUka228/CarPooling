package com.carpooling.dao.postgres;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.carpooling.constants.Constants.*;

@Slf4j
public class PostgresBookingDao extends AbstractPostgresDao<Booking, UUID> implements BookingDao {

    public PostgresBookingDao(SessionFactory sessionFactory) {
        super(sessionFactory, Booking.class);
    }

    @Override
    public String createBooking(Booking booking) throws DataAccessException {
        persistEntity(booking);
        if (booking.getId() == null) {
            throw new DataAccessException("Failed to generate ID for booking");
        }
        return booking.getId().toString();
    }

    @Override
    public Optional<Booking> getBookingById(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "booking id");
        // Нужно ли здесь JOIN FETCH trip/user?
        // return findEntityById(uuid); // Простой get

        log.debug("Looking up Booking by id {} with details", id);
        try {
            Query<Booking> query = getCurrentSession().createQuery(FIND_BOOKING_BY_ID_WITH_DETAILS_HQL, Booking.class);
            query.setParameter("bookingId", uuid);
            return query.uniqueResultOptional();
        } catch (PersistenceException e) {
            log.error("Error reading Booking by id {}: {}", id, e.getMessage());
            throw new DataAccessException("Error reading Booking", e);
        }
    }

    @Override
    public void updateBooking(Booking booking) throws DataAccessException {
        mergeEntity(booking);
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "booking id");
        deleteEntityById(uuid);
    }

    @Override
    public int countBookedSeatsForTrip(String tripId) throws DataAccessException {
        log.debug("Counting booked seats for trip ID: {}", tripId);
        UUID tripUUID = parseUUID(tripId, "trip ID");
        try {
            Session session = getCurrentSession();
            Query<Long> query = session.createQuery(COUNT_BOOKED_SEATS_HQL, Long.class);
            query.setParameter("tripId", tripUUID);
            // uniqueResult() вернет null, если нет результатов, getSingleResult() кинет исключение
            Long result = query.uniqueResult();
            // Если uniqueResult вернул null (нет броней), SUM будет null, COALESCE вернет 0.
            // Если запрос ничего не вернул, result будет null.
            return (result != null) ? result.intValue() : 0;
        } catch (PersistenceException e) {
            log.error("Error counting booked seats for trip {}: {}", tripId, e.getMessage());
            throw new DataAccessException("Error counting booked seats", e);
        }
    }

    @Override
    public List<Booking> findBookingsByUserId(String userId) throws DataAccessException {
        log.debug("Finding bookings for user ID: {}", userId);
        UUID userUUID = parseUUID(userId, "user ID");
        try {
            Session session = getCurrentSession();
            Query<Booking> query = session.createQuery(FIND_BOOKINGS_BY_USER_HQL, Booking.class);
            query.setParameter("userId", userUUID);
            return query.list();
        } catch (PersistenceException e) {
            log.error("Error finding bookings for user {}: {}", userId, e.getMessage());
            throw new DataAccessException("Error finding bookings by user", e);
        }
    }

    @Override
    public Optional<Booking> findBookingByUserAndTrip(String userId, String tripId) throws DataAccessException {
        log.debug("Finding booking for user ID {} and trip ID {}", userId, tripId);
        UUID userUUID = parseUUID(userId, "user ID");
        UUID tripUUID = parseUUID(tripId, "trip ID");
        try {
            Session session = getCurrentSession();
            Query<Booking> query = session.createQuery(FIND_BOOKING_BY_USER_AND_TRIP_HQL, Booking.class);
            query.setParameter("userId", userUUID);
            query.setParameter("tripId", tripUUID);
            return query.uniqueResultOptional();
        } catch (PersistenceException e) {
            log.error("Error finding booking by user {} and trip {}: {}", userId, tripId, e.getMessage());
            throw new DataAccessException("Error finding booking by user and trip", e);
        }
    }
}
