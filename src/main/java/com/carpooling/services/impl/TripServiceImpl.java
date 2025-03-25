package com.carpooling.services.impl;

import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.*;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.base.TripService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripDao tripDao;

    @Override
    public String createTrip(@NotNull Trip trip) throws TripServiceException {
        try {
            String tripId = tripDao.createTrip(trip);
            log.info("Trip created successfully: {}", tripId);
            return tripId;
        } catch (DataAccessException e) {
            log.error("Error creating trip: {}", e.getMessage());
            throw new TripServiceException("Error creating trip", e);
        }
    }

    @Override
    public Optional<Trip> getTripById(String tripId) throws TripServiceException {
        try {
            Optional<Trip> tripOptional = tripDao.getTripById(tripId);
            if (tripOptional.isPresent()) {
                log.info("Trip found: {}", tripId);
            } else {
                log.warn("Trip not found: {}", tripId);
            }
            return tripOptional;
        } catch (DataAccessException e) {
            log.error("Error reading trip: {}", e.getMessage());
            throw new TripServiceException("Error reading trip", e);
        }
    }

    @Override
    public List<Trip> getAllTrips() throws TripServiceException {
        try {
            List<Trip> trips = Collections.emptyList();
            log.info("Retrieved {} trips successfully", trips.size());
            return trips;
        } catch (Exception e) {
            log.error("Error retrieving all trips: {}", e.getMessage());
            throw new TripServiceException("Error retrieving all trips", e);
        }
    }

    @Override
    public void updateTrip(@NotNull Trip trip) throws TripServiceException {
        try {
            Optional<Trip> existingTrip = tripDao.getTripById(trip.getId().toString());
            if (existingTrip.isEmpty()) {
                log.warn("Trip not found for update: {}", trip.getId());
                throw new TripServiceException("Trip not found");
            }
            tripDao.updateTrip(trip);
            log.info("Trip updated successfully: {}", trip.getId());
        } catch (DataAccessException e) {
            log.error("Error updating trip: {}", e.getMessage());
            throw new TripServiceException("Error updating trip", e);
        }
    }

    @Override
    public void deleteTrip(String tripId) throws TripServiceException {
        try {
            tripDao.deleteTrip(tripId);
            log.info("Trip deleted successfully: {}", tripId);
        } catch (DataAccessException e) {
            log.error("Error deleting trip: {}", e.getMessage());
            throw new TripServiceException("Error deleting trip", e);
        }
    }

    @Override
    public List<Trip> getTripsByUser(String userId) throws TripServiceException {
        try {
            List<Trip> trips = Collections.emptyList();
            log.info("Retrieved {} trips for user: {}", trips.size(), userId);
            return trips;
        } catch (Exception e) {
            log.error("Error retrieving trips by user: {}", e.getMessage());
            throw new TripServiceException("Error retrieving trips by user", e);
        }
    }

    @Override
    public List<Trip> getTripsByStatus(String status) throws TripServiceException {
        try  {
            List<Trip> trips = Collections.emptyList();
            log.info("Retrieved {} trips with status: {}", trips.size(), status);
            return trips;
        } catch (Exception e) {
            log.error("Error retrieving trips by status: {}", e.getMessage());
            throw new TripServiceException("Error retrieving trips by status", e);
        }
    }

    @Override
    public List<Trip> getTripsByCreationDate(String date) throws TripServiceException {
        try {
            List<Trip> trips = Collections.emptyList();
            log.info("Retrieved {} trips created on: {}", trips.size(), date);
            return trips;
        } catch (Exception e) {
            log.error("Error retrieving trips by creation date: {}", e.getMessage());
            throw new TripServiceException("Error retrieving trips by creation date", e);
        }
    }

    @Override
    public List<Trip> getTripsByRoute(String routeId) throws TripServiceException {
        try  {
            List<Trip> trips = Collections.emptyList();
            log.info("Retrieved {} trips for route: {}", trips.size(), routeId);
            return trips;
        } catch (Exception e) {
            log.error("Error retrieving trips by route: {}", e.getMessage());
            throw new TripServiceException("Error retrieving trips by route", e);
        }
    }
}