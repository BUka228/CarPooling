package com.carpooling.dao.mongo;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class MongoTripDao extends AbstractMongoDao<Trip> implements TripDao {

    public MongoTripDao(MongoCollection<Document> collection) {
        super(collection, Trip.class);
    }

    @Override
    public String createTrip(Trip trip) throws DataAccessException {
        try {
            trip.setId(UUID.randomUUID());
            Document document = toDocument(trip);
            collection.insertOne(document);

            String id = document.getString("id");
            log.info("Trip created successfully: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating trip: {}", e.getMessage());
            throw new DataAccessException("Error creating trip", e);
        }
    }

    @Override
    public Optional<Trip> getTripById(String id) throws DataAccessException {
        try {
            Document result = collection.find(Filters.eq("id", id)).first();
            if (result != null) {
                Trip trip = fromDocument(result);
                log.info("Trip found: {}", id);
                return Optional.of(trip);
            } else {
                log.warn("Trip not found: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error reading trip: {}", e.getMessage());
            throw new DataAccessException("Error reading trip", e);
        }
    }

    @Override
    public void updateTrip(Trip trip) throws DataAccessException {
        try {
            Document update = toDocument(trip);
            UpdateResult result = collection.updateOne(Filters.eq("id", trip.getId().toString()), new Document("$set", update));
            if (result.getModifiedCount() == 0) {
                log.warn("Trip not found for update: {}", trip.getId());
                throw new DataAccessException("Trip not found");
            }
            log.info("Trip updated successfully: {}", trip.getId());
        } catch (Exception e) {
            log.error("Error updating trip: {}", e.getMessage());
            throw new DataAccessException("Error updating trip", e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("id", id));
            if (result.getDeletedCount() > 0) {
                log.info("Trip deleted successfully: {}", id);
            } else {
                log.warn("Trip not found for deletion: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting trip: {}", e.getMessage());
            throw new DataAccessException("Error deleting trip", e);
        }
    }

    @Override
    public List<Trip> findTrips(String startPoint, String endPoint, LocalDate date) throws DataAccessException, OperationNotSupportedException {
        return List.of();
    }
}