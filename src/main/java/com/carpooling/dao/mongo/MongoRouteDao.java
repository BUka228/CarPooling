package com.carpooling.dao.mongo;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.record.RouteRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.carpooling.constants.Constants.MONGO_ID;
import static com.carpooling.constants.ErrorMessages.ROUTE_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.ROUTE_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class MongoRouteDao extends AbstractMongoDao<RouteRecord> implements RouteDao {

    public MongoRouteDao(MongoCollection<Document> collection) {
        super(collection, RouteRecord.class);
    }

    @Override
    public String createRoute(RouteRecord route) throws DataAccessException{
        try {
            // Преобразуем объект Route в документ для MongoDB
            Document document = toDocument(route);
            collection.insertOne(document);

            // Получаем сгенерированный ObjectId и возвращаем его как строку
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();

            log.info(CREATE_ROUTE_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_ROUTE, route, e);
            throw new DataAccessException(ROUTE_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RouteRecord> getRouteById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();
            if (result != null) {
                RouteRecord route = fromDocument(result);
                log.info(GET_ROUTE_START, id);
                return Optional.of(route);
            } else {
                log.warn(WARN_ROUTE_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_ROUTE, id, e);
            throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRoute(RouteRecord route) throws DataAccessException{
        try {
            ObjectId objectId = new ObjectId(route.getId());
            Document update = toDocument(route);
            update.put(MONGO_ID, objectId);

            collection.updateOne(Filters.eq(MONGO_ID, objectId), new Document("$set", update));
            log.info(UPDATE_ROUTE_SUCCESS, route.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_ROUTE, route.getId(), e);
            throw new DataAccessException(ROUTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_ROUTE_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_ROUTE, id, e);
            throw new DataAccessException(ROUTE_DELETE_ERROR, e);
        }
    }
}
