package com.carpooling.services.impl;

import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.services.base.RatingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;



@Slf4j
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;

    @Override
    public String createRating(Rating rating) throws RatingServiceException {
        try {
            String ratingId = ratingDao.createRating(rating);
            log.info("Rating created successfully: {}", ratingId);
            return ratingId;
        } catch (DataAccessException e) {
            log.error("Error creating rating: {}", e.getMessage());
            throw new RatingServiceException("Error creating rating", e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String ratingId) throws RatingServiceException {
        try {
            Optional<Rating> ratingOptional = ratingDao.getRatingById(ratingId);
            if (ratingOptional.isPresent()) {
                log.info("Rating found: {}", ratingId);
            } else {
                log.warn("Rating not found: {}", ratingId);
            }
            return ratingOptional;
        } catch (DataAccessException e) {
            log.error("Error reading rating: {}", e.getMessage());
            throw new RatingServiceException("Error reading rating", e);
        }
    }

    @Override
    public List<Rating> getAllRatings() throws RatingServiceException {
        try {
            // Заглушка, так как метод не реализован в DAO
            log.warn("Method getAllRatings is not implemented");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error retrieving all ratings: {}", e.getMessage());
            throw new RatingServiceException("Error retrieving all ratings", e);
        }
    }

    @Override
    public void updateRating(@NotNull Rating rating) throws RatingServiceException {
        try {
            ratingDao.updateRating(rating);
            log.info("Rating updated successfully: {}", rating.getId());
        } catch (DataAccessException e) {
            log.error("Error updating rating: {}", e.getMessage());
            throw new RatingServiceException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(String ratingId) throws RatingServiceException {
        try {
            ratingDao.deleteRating(ratingId);
            log.info("Rating deleted successfully: {}", ratingId);
        } catch (DataAccessException e) {
            log.error("Error deleting rating: {}", e.getMessage());
            throw new RatingServiceException("Error deleting rating", e);
        }
    }

    @Override
    public List<Rating> getRatingsByTrip(String tripId) throws RatingServiceException {
        try {
            log.warn("Method getRatingsByTrip is not implemented for trip: {}", tripId);
            throw new UnsupportedOperationException("Method getRatingsByTrip is not implemented");
        } catch (Exception e) {
            log.error("Error retrieving ratings by trip: {}", e.getMessage());
            throw new RatingServiceException("Error retrieving ratings by trip", e);
        }
    }

    @Override
    public List<Rating> getRatingsByRating(int rating) throws RatingServiceException {
        try {
            log.warn("Method getRatingsByRating is not implemented for rating: {}", rating);
            throw new UnsupportedOperationException("Method getRatingsByRating is not implemented");
        } catch (Exception e) {
            log.error("Error retrieving ratings by rating value: {}", e.getMessage());
            throw new RatingServiceException("Error retrieving ratings by rating value", e);
        }
    }

    @Override
    public double getAverageRatingForTrip(String tripId) throws RatingServiceException {
        try {
            log.warn("Method getAverageRatingForTrip is not implemented for trip: {}", tripId);
            throw new UnsupportedOperationException("Method getAverageRatingForTrip is not implemented");
        } catch (Exception e) {
            log.error("Error calculating average rating for trip: {}", e.getMessage());
            throw new RatingServiceException("Error calculating average rating for trip", e);
        }
    }
}
