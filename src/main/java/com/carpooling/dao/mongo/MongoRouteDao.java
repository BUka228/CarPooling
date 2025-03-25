package com.carpooling.dao.mongo;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
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
public class MongoRouteDao extends AbstractMongoDao<Route> implements RouteDao {

    public MongoRouteDao(MongoCollection<Document> collection) {
        super(collection, Route.class);
    }

    @Override
    public String createRoute(Route route) throws DataAccessException {
        try {
            Document document = toDocument(route);
            collection.insertOne(document);

            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();
            log.info("Route created successfully: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating route: {}", e.getMessage());
            throw new DataAccessException("Error creating route", e);
        }
    }

    @Override
    public Optional<Route> getRouteById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();
            if (result != null) {
                Route route = fromDocument(result);
                log.info("Route found: {}", id);
                return Optional.of(route);
            } else {
                log.warn("Route not found: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error reading route: {}", e.getMessage());
            throw new DataAccessException("Error reading route", e);
        }
    }

    @Override
    public void updateRoute(Route route) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(route.getId().toString());
            Document update = toDocument(route);
            UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
            if (result.getModifiedCount() == 0) {
                log.warn("Route not found for update: {}", route.getId());
                throw new DataAccessException("Route not found");
            }
            log.info("Route updated successfully: {}", route.getId());
        } catch (Exception e) {
            log.error("Error updating route: {}", e.getMessage());
            throw new DataAccessException("Error updating route", e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
            if (result.getDeletedCount() > 0) {
                log.info("Route deleted successfully: {}", id);
            } else {
                log.warn("Route not found for deletion: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting route: {}", e.getMessage());
            throw new DataAccessException("Error deleting route", e);
        }
    }
}