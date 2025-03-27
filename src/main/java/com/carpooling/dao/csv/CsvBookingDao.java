package com.carpooling.dao.csv;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class CsvBookingDao extends AbstractCsvDao<Booking> implements BookingDao {

    public CsvBookingDao(String filePath) {
        super(Booking.class, filePath);
    }

    @Override
    public String createBooking(@NotNull Booking booking) throws DataAccessException {
        UUID bookingId = generateId();
        booking.setId(bookingId);
        try {
            List<Booking> bookings = readAll();
            bookings.add(booking);
            writeAll(bookings);
            log.info("Booking created successfully: {}", bookingId);
            return bookingId.toString();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw new DataAccessException("Error creating booking", e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(String id) throws DataAccessException {
        try {
            Optional<Booking> booking = findById(record -> record.getId().toString().equals(id));
            if (booking.isPresent()) {
                log.info("Booking found: {}", id);
            } else {
                log.warn("Booking not found: {}", id);
            }
            return booking;
        } catch (IOException e) {
            log.error("Error reading booking: {}", e.getMessage());
            throw new DataAccessException("Error reading booking", e);
        }
    }

    @Override
    public void updateBooking(@NotNull Booking booking) throws DataAccessException {
        try {
            boolean updated = updateItem(record -> record.getId().equals(booking.getId()), booking);
            if (!updated) {
                log.warn("Booking not found for update: {}", booking.getId());
                throw new DataAccessException("Booking not found");
            }
            log.info("Booking updated successfully: {}", booking.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error updating booking: {}", e.getMessage());
            throw new DataAccessException("Error updating booking", e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("Booking deleted successfully: {}", id);
            } else {
                log.warn("Booking not found for deletion: {}", id);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error deleting booking: {}", e.getMessage());
            throw new DataAccessException("Error deleting booking", e);
        }
    }

    @Override
    public int countBookedSeatsForTrip(String tripId) throws DataAccessException, OperationNotSupportedException {
        return 0;
    }

    @Override
    public List<Booking> findBookingsByUserId(String userId) throws DataAccessException, OperationNotSupportedException {
        return List.of();
    }

    @Override
    public Optional<Booking> findBookingByUserAndTrip(String userId, String tripId) throws DataAccessException, OperationNotSupportedException {
        return Optional.empty();
    }
}