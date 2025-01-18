package com.carpooling.services.impl;

import com.carpooling.cli.context.CliContext;
import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.RatingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса RatingService.
 * Предоставляет методы для работы с оценками, включая создание, получение, обновление и удаление.
 */
@Slf4j
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;

    public RatingServiceImpl() {
        this.ratingDao = DaoFactory.getRatingDao(CliContext.getCurrentStorageType());
    }

    @Override
    public String createRating(Rating rating, String tripId) throws RatingServiceException {
        log.info(LogMessages.RATING_CREATION_START, tripId);
        try {
            RatingRecord ratingRecord = new RatingRecord(rating, tripId);
            String ratingId = ratingDao.createRating(ratingRecord);
            log.info(LogMessages.RATING_CREATION_SUCCESS, ratingId);
            return ratingId;
        } catch (Exception e) {
            log.error(LogMessages.RATING_CREATION_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String ratingId) throws RatingServiceException {
        log.info(LogMessages.RATING_SEARCH_BY_ID_START, ratingId);
        try {
            Optional<RatingRecord> ratingOptional = ratingDao.getRatingById(ratingId);
            if (ratingOptional.isEmpty()) {
                log.warn(LogMessages.RATING_SEARCH_BY_ID_ERROR, ratingId);
            }
            log.info(LogMessages.RATING_SEARCH_BY_ID_SUCCESS, ratingId);
            return ratingOptional.map(RatingRecord::toRating);
        } catch (Exception e) {
            log.error(LogMessages.RATING_SEARCH_BY_ID_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Rating> getAllRatings() throws RatingServiceException {
        log.info(LogMessages.RATING_GET_ALL_START);
        try {
            List<Rating> ratings = Collections.emptyList();
            log.info(LogMessages.RATING_GET_ALL_SUCCESS);
            return ratings;
        } catch (Exception e) {
            log.error(LogMessages.RATING_GET_ALL_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_GET_ALL_ERROR, e);
        }
    }

    @Override
    public void updateRating(@NotNull Rating rating, String tripId) throws RatingServiceException {
        log.info(LogMessages.RATING_UPDATE_START, rating.getId());
        try {
            RatingRecord ratingRecord = new RatingRecord(rating, tripId);
            ratingDao.updateRating(ratingRecord);
            log.info(LogMessages.RATING_UPDATE_SUCCESS, rating.getId());
        } catch (Exception e) {
            log.error(LogMessages.RATING_UPDATE_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRating(String ratingId) throws RatingServiceException {
        log.info(LogMessages.RATING_DELETION_START, ratingId);
        try {
            ratingDao.deleteRating(ratingId);
            log.info(LogMessages.RATING_DELETION_SUCCESS, ratingId);
        } catch (Exception e) {
            log.error(LogMessages.RATING_DELETION_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_DELETION_ERROR, e);
        }
    }

    @Override
    public List<Rating> getRatingsByTrip(String tripId) throws RatingServiceException {
        log.info(LogMessages.RATING_GET_BY_TRIP_START, tripId);
        try {
            throw new UnsupportedOperationException("Метод getRatingsByTrip не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.RATING_GET_BY_TRIP_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_GET_BY_TRIP_ERROR, e);
        }
    }

    @Override
    public List<Rating> getRatingsByRating(int rating) throws RatingServiceException {
        log.info(LogMessages.RATING_GET_BY_RATING_START, rating);
        try {
            throw new UnsupportedOperationException("Метод getRatingsByRating не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.RATING_GET_BY_RATING_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_GET_BY_RATING_ERROR, e);
        }
    }

    @Override
    public double getAverageRatingForTrip(String tripId) throws RatingServiceException {
        log.info(LogMessages.RATING_GET_AVERAGE_START, tripId);
        try {
            throw new UnsupportedOperationException("Метод getAverageRatingForTrip не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.RATING_GET_AVERAGE_ERROR, e.getMessage());
            throw new RatingServiceException(ErrorMessages.RATING_GET_AVERAGE_ERROR, e);
        }
    }
}
