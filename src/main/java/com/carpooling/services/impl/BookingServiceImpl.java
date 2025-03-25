package com.carpooling.services.impl;


import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.BookingServiceException;
import com.carpooling.services.base.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;

    @Override
    public String createBooking(@NotNull Booking booking) throws BookingServiceException {
        try {
            String bookingId = bookingDao.createBooking(booking);
            log.info("Booking created successfully: {}", bookingId);
            return bookingId;
        } catch (DataAccessException e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw new BookingServiceException("Error creating booking", e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(String bookingId) throws BookingServiceException {
        try {
            Optional<Booking> bookingOptional = bookingDao.getBookingById(bookingId);
            if (bookingOptional.isPresent()) {
                log.info("Booking found: {}", bookingId);
            } else {
                log.warn("Booking not found: {}", bookingId);
            }
            return bookingOptional;
        } catch (DataAccessException e) {
            log.error("Error reading booking: {}", e.getMessage());
            throw new BookingServiceException("Error reading booking", e);
        }
    }

    @Override
    public List<Booking> getAllBookings() throws BookingServiceException {
        try {
            // Заглушка, так как метод не реализован в DAO
            log.warn("Method getAllBookings is not implemented");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error retrieving all bookings: {}", e.getMessage());
            throw new BookingServiceException("Error retrieving all bookings", e);
        }
    }

    @Override
    public void updateBooking(@NotNull Booking booking) throws BookingServiceException {
        try {
            bookingDao.updateBooking(booking);
            log.info("Booking updated successfully: {}", booking.getId());
        } catch (DataAccessException e) {
            log.error("Error updating booking: {}", e.getMessage());
            throw new BookingServiceException("Error updating booking", e);
        }
    }

    @Override
    public void deleteBooking(String bookingId) throws BookingServiceException {
        try {
            bookingDao.deleteBooking(bookingId);
            log.info("Booking deleted successfully: {}", bookingId);
        } catch (DataAccessException e) {
            log.error("Error deleting booking: {}", e.getMessage());
            throw new BookingServiceException("Error deleting booking", e);
        }
    }

    @Override
    public List<Booking> getBookingsByTrip(String tripId) throws BookingServiceException {
        try {
            log.warn("Method getBookingsByTrip is not implemented for trip: {}", tripId);
            throw new UnsupportedOperationException("Method getBookingsByTrip is not implemented");
        } catch (Exception e) {
            log.error("Error retrieving bookings by trip: {}", e.getMessage());
            throw new BookingServiceException("Error retrieving bookings by trip", e);
        }
    }

    @Override
    public List<Booking> getBookingsByUser(String userId) throws BookingServiceException {
        try {
            log.warn("Method getBookingsByUser is not implemented for user: {}", userId);
            throw new UnsupportedOperationException("Method getBookingsByUser is not implemented");
        } catch (Exception e) {
            log.error("Error retrieving bookings by user: {}", e.getMessage());
            throw new BookingServiceException("Error retrieving bookings by user", e);
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) throws BookingServiceException {
        try {
            log.warn("Method getBookingsByStatus is not implemented for status: {}", status);
            throw new UnsupportedOperationException("Method getBookingsByStatus is not implemented");
        } catch (Exception e) {
            log.error("Error retrieving bookings by status: {}", e.getMessage());
            throw new BookingServiceException("Error retrieving bookings by status", e);
        }
    }

    @Override
    public void cancelBooking(String bookingId) throws BookingServiceException {
        try {
            log.warn("Method cancelBooking is not implemented for booking: {}", bookingId);
            throw new UnsupportedOperationException("Method cancelBooking is not implemented");
        } catch (Exception e) {
            log.error("Error cancelling booking: {}", e.getMessage());
            throw new BookingServiceException("Error cancelling booking", e);
        }
    }
}