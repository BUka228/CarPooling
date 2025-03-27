package com.carpooling.dao.csv;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CsvTripDao extends AbstractCsvDao<Trip> implements TripDao {

    public CsvTripDao(String filePath) {
        super(Trip.class, filePath);
    }

    @Override
    public String createTrip(@NotNull Trip trip) throws DataAccessException {
        UUID tripId = generateId();
        trip.setId(tripId);

        try {
            List<Trip> trips = readAll();
            trips.add(trip);
            writeAll(trips);
            log.info("Trip created successfully: {}", tripId);
            return tripId.toString();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error creating trip: {}", e.getMessage());
            throw new DataAccessException("Error creating trip", e);
        }
    }

    @Override
    public Optional<Trip> getTripById(String id) throws DataAccessException {
        try {
            Optional<Trip> trip = findById(record -> record.getId().toString().equals(id));
            if (trip.isPresent()) {
                log.info("Trip found: {}", id);
            } else {
                log.warn("Trip not found: {}", id);
            }
            return trip;
        } catch (IOException e) {
            log.error("Error reading trip: {}", e.getMessage());
            throw new DataAccessException("Error reading trip", e);
        }
    }

    @Override
    public void updateTrip(@NotNull Trip trip) throws DataAccessException {
        try {
            boolean updated = updateItem(record -> record.getId().equals(trip.getId()), trip);
            if (!updated) {
                log.warn("Trip not found for update: {}", trip.getId());
                throw new DataAccessException("Trip not found");
            }
            log.info("Trip updated successfully: {}", trip.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error updating trip: {}", e.getMessage());
            throw new DataAccessException("Error updating trip", e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("Trip deleted successfully: {}", id);
            } else {
                log.warn("Trip not found for deletion: {}", id);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error deleting trip: {}", e.getMessage());
            throw new DataAccessException("Error deleting trip", e);
        }
    }

    @Override
    public List<Trip> findTrips(String startPoint, String endPoint, LocalDate date) throws DataAccessException, OperationNotSupportedException {
        return List.of();
    }
}