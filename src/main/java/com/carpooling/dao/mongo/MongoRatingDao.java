package com.carpooling.dao.mongo;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
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
public class MongoRatingDao extends AbstractMongoDao<Rating> implements RatingDao {

    public MongoRatingDao(MongoCollection<Document> collection) {
        super(collection, Rating.class);
    }

    @Override
    public String createRating(Rating rating) throws DataAccessException {
        try {
            Document document = toDocument(rating);
            collection.insertOne(document);

            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();
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
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();
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
            ObjectId objectId = new ObjectId(rating.getId().toString());
            Document update = toDocument(rating);
            UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
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
            ObjectId objectId = new ObjectId(id);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
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
}

