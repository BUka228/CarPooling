package com.carpooling.dao.csv;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
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
public class CsvRatingDao extends AbstractCsvDao<Rating> implements RatingDao {

    public CsvRatingDao(String filePath) {
        super(Rating.class, filePath);
    }

    @Override
    public String createRating(@NotNull Rating rating) throws DataAccessException {
        UUID ratingId = generateId();
        rating.setId(ratingId);

        try {
            List<Rating> ratings = readAll();
            ratings.add(rating);
            writeAll(ratings);
            log.info("Rating created successfully: {}", ratingId);
            return ratingId.toString();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error creating rating: {}", e.getMessage());
            throw new DataAccessException("Error creating rating", e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String id) throws DataAccessException {
        try {
            Optional<Rating> rating = findById(record -> record.getId().toString().equals(id));
            if (rating.isPresent()) {
                log.info("Rating found: {}", id);
            } else {
                log.warn("Rating not found: {}", id);
            }
            return rating;
        } catch (IOException e) {
            log.error("Error reading rating: {}", e.getMessage());
            throw new DataAccessException("Error reading rating", e);
        }
    }

    @Override
    public void updateRating(@NotNull Rating rating) throws DataAccessException {
        try {
            boolean updated = updateItem(record -> record.getId().equals(rating.getId()), rating);
            if (!updated) {
                log.warn("Rating not found for update: {}", rating.getId());
                throw new DataAccessException("Rating not found");
            }
            log.info("Rating updated successfully: {}", rating.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error updating rating: {}", e.getMessage());
            throw new DataAccessException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("Rating deleted successfully: {}", id);
            } else {
                log.warn("Rating not found for deletion: {}", id);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error deleting rating: {}", e.getMessage());
            throw new DataAccessException("Error deleting rating", e);
        }
    }

    @Override
    public List<Rating> findRatingsByTripId(String tripId) throws DataAccessException, OperationNotSupportedException {
        return List.of();
    }

    @Override
    public Optional<Rating> findRatingByUserAndTrip(String userId, String tripId) throws DataAccessException, OperationNotSupportedException {
        return Optional.empty();
    }
}