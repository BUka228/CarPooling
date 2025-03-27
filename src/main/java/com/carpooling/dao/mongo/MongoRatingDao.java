package com.carpooling.dao.mongo;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class MongoRatingDao extends AbstractMongoDao<Rating> implements RatingDao {

    public MongoRatingDao(MongoCollection<Document> collection) {
        super(collection, Rating.class);
    }

    @Override
    public String createRating(Rating rating) throws DataAccessException {
        try {
            rating.setId(UUID.randomUUID());
            Document document = toDocument(rating);
            collection.insertOne(document);

            String id = document.getString("id");
            log.info("Rating created successfully: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating rating: {}", e.getMessage());
            throw new DataAccessException("Error creating rating", e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String id) throws DataAccessException {
        try {
            Document result = collection.find(Filters.eq("id", id)).first();
            if (result != null) {
                Rating rating = fromDocument(result);
                log.info("Rating found: {}", id);
                return Optional.of(rating);
            } else {
                log.warn("Rating not found: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error reading rating: {}", e.getMessage());
            throw new DataAccessException("Error reading rating", e);
        }
    }

    @Override
    public void updateRating(Rating rating) throws DataAccessException {
        try {
            Document update = toDocument(rating);
            UpdateResult result = collection.updateOne(Filters.eq("id", rating.getId().toString()), new Document("$set", update));
            if (result.getModifiedCount() == 0) {
                log.warn("Rating not found for update: {}", rating.getId());
                throw new DataAccessException("Rating not found");
            }
            log.info("Rating updated successfully: {}", rating.getId());
        } catch (Exception e) {
            log.error("Error updating rating: {}", e.getMessage());
            throw new DataAccessException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("id", id));
            if (result.getDeletedCount() > 0) {
                log.info("Rating deleted successfully: {}", id);
            } else {
                log.warn("Rating not found for deletion: {}", id);
            }
        } catch (Exception e) {
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

