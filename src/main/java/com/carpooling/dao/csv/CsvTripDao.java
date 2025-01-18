package com.carpooling.dao.csv;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.record.TripRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.TRIP_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.TRIP_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;


@Slf4j
public class CsvTripDao extends AbstractCsvDao<TripRecord> implements TripDao {

    public CsvTripDao(String filePath) {
        super(TripRecord.class, filePath);
    }

    @Override
    public String createTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        log.info(CREATE_TRIP_START, tripRecord.getUserId(), tripRecord.getRouteId());

        // Генерация UUID для ID
        String tripId = generateId();
        tripRecord.setId(tripId);

        try {
            List<TripRecord> trips = readAll();
            trips.add(tripRecord);
            writeAll(trips);

            log.info(CREATE_TRIP_SUCCESS, tripId);
            return tripId;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_CREATE_TRIP, tripRecord.getUserId(), tripRecord.getRouteId(), e);
            throw new DataAccessException(TRIP_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<TripRecord> getTripById(String id) throws DataAccessException {
        log.info(GET_TRIP_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (IOException e) {
            log.error(ERROR_GET_TRIP, id, e);
            throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        try {
            List<TripRecord> trips = readAll();
            boolean found = false;
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).getId().equals(tripRecord.getId())) {
                    trips.set(i, tripRecord);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.warn(WARN_TRIP_NOT_FOUND, tripRecord.getId());
                throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, tripRecord.getId()));
            }
            writeAll(trips);
            log.info(UPDATE_TRIP_SUCCESS, tripRecord.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_UPDATE_TRIP, tripRecord.getId(), e);
            throw new DataAccessException(TRIP_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_TRIP_SUCCESS, id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_DELETE_TRIP, id, e);
            throw new DataAccessException(TRIP_DELETE_ERROR, e);
        }
    }
}