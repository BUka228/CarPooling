package com.carpooling.services.impl;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.entities.enums.BookingStatus;
import com.carpooling.entities.enums.TripStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.BookingException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.services.base.BookingService;
import com.carpooling.transaction.DataAccessManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;
    private final TripDao tripDao;
    private final UserDao userDao;
    private final DataAccessManager dataAccessManager;

    public BookingServiceImpl(BookingDao bookingDao, TripDao tripDao, UserDao userDao, DataAccessManager dataAccessManager) {
        this.bookingDao = bookingDao;
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public String createBooking(String userId, String tripId, byte numberOfSeats, String passportNumber, LocalDate passportExpiry)
            throws BookingException, OperationNotSupportedException, DataAccessException {
        log.debug("Attempting to create booking for user ID {} on trip ID {}", userId, tripId);

        if (numberOfSeats <= 0) {
            throw new BookingException("Количество мест должно быть положительным.");
        }

        // Выполняем в транзакции
        return dataAccessManager.executeInTransaction(() -> {
            // 1. Get User and Trip
            Optional<User> userOpt = userDao.getUserById(userId);
            User booker = userOpt.orElseThrow(() -> new BookingException("Пользователь с ID " + userId + " не найден."));

            Optional<Trip> tripOpt = tripDao.getTripById(tripId);
            Trip trip = tripOpt.orElseThrow(() -> new BookingException("Поездка с ID " + tripId + " не найдена."));

            // Проверка статуса поездки
            if (trip.getStatus() != TripStatus.PLANNED && trip.getStatus() != TripStatus.ACTIVE) { // Можно ли бронировать активные?
                throw new BookingException("Нельзя забронировать место на отмененную или завершенную поездку (статус: " + trip.getStatus() + ").");
            }
            // Проверка времени отправления (если нужно)
            if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
                throw new BookingException("Нельзя забронировать место на уже отправившуюся поездку.");
            }

            // 2. Проверка мест
            int bookedSeats;
            try {
                bookedSeats = bookingDao.countBookedSeatsForTrip(tripId);
            } catch (OperationNotSupportedException e) {
                log.warn("Seat availability check skipped for trip {}. DAO does not support countBookedSeatsForTrip.", tripId);
                bookedSeats = -1;
            }

            if (bookedSeats != -1) {
                if (bookedSeats + numberOfSeats > trip.getMaxPassengers()) {
                    throw new BookingException("Недостаточно свободных мест (" + (trip.getMaxPassengers() - bookedSeats) + ").");
                }
            } else if (numberOfSeats > trip.getMaxPassengers()) {
                throw new BookingException("Запрошено больше мест ("+ numberOfSeats +"), чем доступно (" + trip.getMaxPassengers() + ").");
            }

            // 3. Проверка дубликата бронирования
            try {
                if (bookingDao.findBookingByUserAndTrip(userId, tripId).isPresent()) {
                    throw new BookingException("Вы уже забронировали место на эту поездку.");
                }
            } catch (OperationNotSupportedException e) {
                log.warn("Duplicate booking check skipped. DAO does not support findBookingByUserAndTrip.");
            }

            // 4. Create Booking object
            Booking booking = new Booking();
            booking.setUser(booker);
            booking.setTrip(trip);
            booking.setNumberOfSeats(numberOfSeats);
            booking.setPassportNumber(passportNumber);
            booking.setPassportExpiryDate(passportExpiry);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setBookingDate(LocalDateTime.now());

            // 5. Save Booking
            String bookingId = bookingDao.createBooking(booking);
            log.info("Booking created successfully: ID={}", bookingId);
            return bookingId;
        });
    }

    @Override
    public Optional<Booking> getBookingById(String bookingId) throws DataAccessException {
        log.debug("Fetching booking by ID: {}", bookingId);
        return dataAccessManager.executeReadOnly(() ->
                bookingDao.getBookingById(bookingId)
        );
    }

    @Override
    public void cancelBooking(String bookingId, String userId) throws DataAccessException {
        log.debug("Attempting to cancel booking ID: {} by user ID: {}", bookingId, userId);

        dataAccessManager.executeInTransaction(() -> {
            Optional<Booking> bookingOpt = bookingDao.getBookingById(bookingId);
            Booking booking = bookingOpt.orElseThrow(() -> new BookingException("Бронирование с ID " + bookingId + " не найдено."));

            // 1. Проверка прав
            if (booking.getUser() == null || !booking.getUser().getId().toString().equals(userId)) {
                throw new BookingException("У вас нет прав для отмены этого бронирования.");
            }

            // 2. Проверка статуса
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                log.info("Booking {} is already CANCELLED.", bookingId);
                return null;
            }
            // Доп. проверки: нельзя отменить завершенную поездку?
            if (booking.getTrip() != null && booking.getTrip().getStatus() == TripStatus.COMPLETED) {
                throw new BookingException("Нельзя отменить бронирование на завершенную поездку.");
            }
            // Нельзя отменить уже уехавшую поездку?
            if (booking.getTrip() != null && booking.getTrip().getDepartureTime().isBefore(LocalDateTime.now())) {
                // Политика может быть разной - разрешить отмену до N часов до отправления и т.д.
                log.warn("Attempting to cancel booking {} for a trip that has already departed.", bookingId);
                // throw new BookingException("Нельзя отменить бронирование на уже отправившуюся поездку.");
            }

            // 3. Обновление статуса
            booking.setStatus(BookingStatus.CANCELLED);
            log.trace("Setting booking {} status to CANCELLED", bookingId);

            // 4. Сохранение
            bookingDao.updateBooking(booking);
            log.info("Booking {} cancelled successfully by user {}", bookingId, userId);
            return null;
        });
    }

    @Override
    public List<Booking> findBookingsByUserId(String userId) throws DataAccessException {
        log.debug("Attempting to find bookings for user ID: {}", userId);
        return dataAccessManager.executeReadOnly(() ->
                bookingDao.findBookingsByUserId(userId)
        );
    }
}