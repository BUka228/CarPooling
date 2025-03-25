package com.carpooling.dao.mongo;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

@Slf4j
public class MongoTripDao extends AbstractMongoDao<Trip> implements TripDao {

    public MongoTripDao(MongoCollection<Document> collection) {
        super(collection, Trip.class);
    }

    @Override
    public String createTrip(Trip trip) throws DataAccessException {
        try {
            Document document = toDocument(trip);
            collection.insertOne(document);

            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();
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
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();
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
            ObjectId objectId = new ObjectId(trip.getId().toString());
            Document update = toDocument(trip);
            UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
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
            ObjectId objectId = new ObjectId(id);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
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
}