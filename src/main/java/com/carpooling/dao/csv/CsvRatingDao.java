package com.carpooling.dao.csv;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.RATING_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.RATING_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class CsvRatingDao extends AbstractCsvDao<RatingRecord> implements RatingDao {

    public CsvRatingDao(String filePath) {
        super(RatingRecord.class, filePath);
    }

    @Override
    public String createRating(@NotNull RatingRecord ratingRecord) throws DataAccessException {
        log.info(CREATE_RATING_START, ratingRecord.getTripId());

        // Генерация UUID для ID
        String ratingId = generateId();
        ratingRecord.setId(ratingId);

        try {
            List<RatingRecord> ratings = readAll();
            ratings.add(ratingRecord);
            writeAll(ratings);

            log.info(CREATE_RATING_SUCCESS, ratingId);
            return ratingId;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_CREATE_RATING, ratingRecord.getTripId(), e);
            throw new DataAccessException(RATING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RatingRecord> getRatingById(String id) throws DataAccessException {
        log.info(GET_RATING_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (IOException e) {
            log.error(ERROR_GET_RATING, id, e);
            throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRating(@NotNull RatingRecord ratingRecord) throws DataAccessException {
        try {
            List<RatingRecord> ratings = readAll();
            boolean found = false;
            for (int i = 0; i < ratings.size(); i++) {
                if (ratings.get(i).getId().equals(ratingRecord.getId())) {
                    ratings.set(i, ratingRecord);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.warn(WARN_RATING_NOT_FOUND, ratingRecord.getId());
                throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, ratingRecord.getId()));
            }
            writeAll(ratings);
            log.info(UPDATE_RATING_SUCCESS, ratingRecord.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_UPDATE_RATING, ratingRecord.getId(), e);
            throw new DataAccessException(RATING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_RATING_SUCCESS, id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_DELETE_RATING, id, e);
            throw new DataAccessException(RATING_DELETE_ERROR, e);
        }
    }

}