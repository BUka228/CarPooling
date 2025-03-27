package com.carpooling.services.impl;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.Booking; // Нужен для проверки
import com.carpooling.entities.database.Rating;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.RatingException;
import com.carpooling.services.base.RatingService;
import com.carpooling.transaction.DataAccessManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections; // Для Collections.emptyList()
import java.util.List;
import java.util.Optional;

@Slf4j
public class RatingServiceImpl implements RatingService {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final RatingDao ratingDao;
    private final TripDao tripDao;
    private final UserDao userDao;
    private final BookingDao bookingDao;
    private final DataAccessManager dataAccessManager;

    public RatingServiceImpl(RatingDao ratingDao, TripDao tripDao, UserDao userDao, BookingDao bookingDao, DataAccessManager dataAccessManager) {
        this.ratingDao = ratingDao;
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.bookingDao = bookingDao;
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public String createRating(String userId, String tripId, int ratingValue, String comment)
            throws RatingException, OperationNotSupportedException, DataAccessException {
        log.debug("Attempting to create rating for trip ID {} by user ID {}", tripId, userId);

        if (ratingValue < MIN_RATING || ratingValue > MAX_RATING) {
            throw new RatingException("Рейтинг должен быть от " + MIN_RATING + " до " + MAX_RATING + ".");
        }

        return dataAccessManager.executeInTransaction(() -> {
            // 1. Get User and Trip
            Optional<User> userOpt = userDao.getUserById(userId);
            User rater = userOpt.orElseThrow(() -> new RatingException("Пользователь с ID " + userId + " не найден."));

            Optional<Trip> tripOpt = tripDao.getTripById(tripId);
            Trip trip = tripOpt.orElseThrow(() -> new RatingException("Поездка с ID " + tripId + " не найдена."));

            // Доп. проверка: можно ли оценить поездку в текущем статусе?
            // if (trip.getStatus() != TripStatus.COMPLETED) {
            //     throw new RatingException("Оценивать можно только завершенные поездки.");
            // }

            // 2. Проверка участия пользователя в поездке
            try {
                Optional<Booking> bookingOpt = bookingDao.findBookingByUserAndTrip(userId, tripId);
                if (bookingOpt.isEmpty()) {
                    throw new RatingException("Вы не можете оценить поездку, в которой не участвовали.");
                }
                // Можно проверить статус бронирования, если нужно
                // if(bookingOpt.get().getStatus() != BookingStatus.COMPLETED && bookingOpt.get().getStatus() != BookingStatus.CONFIRMED) { ... }
                log.trace("User {} participation in trip {} confirmed.", userId, tripId);
            } catch (OperationNotSupportedException e) {
                log.warn("User participation check skipped. DAO does not support findBookingByUserAndTrip.");
            }

            // 3. Проверка, что пользователь еще не оценивал эту поездку
            try {
                if (ratingDao.findRatingByUserAndTrip(userId, tripId).isPresent()) {
                    throw new RatingException("Вы уже оценили эту поездку.");
                }
                log.trace("User {} has not rated trip {} before.", userId, tripId);
            } catch (OperationNotSupportedException e) {
                log.warn("Existing rating check skipped. DAO does not support findRatingByUserAndTrip.");
            }

            // 4. Create Rating object
            Rating rating = new Rating();
            rating.setTrip(trip);
            // rating.setUser(rater); // Если есть связь
            rating.setRating(ratingValue);
            rating.setComment(comment);
            rating.setDate(LocalDateTime.now());

            // 5. Save Rating
            String ratingId = ratingDao.createRating(rating);
            log.info("Rating created successfully: ID={}", ratingId);
            return ratingId;
        });
    }

    @Override
    public Optional<Rating> getRatingById(String ratingId) throws DataAccessException {
        log.debug("Fetching rating by ID: {}", ratingId);
        return dataAccessManager.executeReadOnly(() ->
                ratingDao.getRatingById(ratingId)
        );
    }

    @Override
    public List<Rating> findRatingsByTripId(String tripId) throws DataAccessException {
        log.debug("Attempting to find ratings for trip ID: {}", tripId);
        return dataAccessManager.executeReadOnly(() ->
                ratingDao.findRatingsByTripId(tripId)
        );
    }
}