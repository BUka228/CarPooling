package com.carpooling.dao.mongo;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.record.TripRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.carpooling.constants.Constants.*;
import static com.carpooling.constants.ErrorMessages.TRIP_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.TRIP_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class MongoTripDao extends AbstractMongoDao<TripRecord> implements TripDao {

    public MongoTripDao(MongoCollection<Document> collection) {
        super(collection, TripRecord.class);
    }

    @Override
    public String createTrip(TripRecord tripRecord) throws DataAccessException {
        try {
            Document document = toDocument(tripRecord);
            document.put(USER_ID, new ObjectId(tripRecord.getUserId()));
            document.put(ROUTE_ID, new ObjectId(tripRecord.getRouteId()));
            collection.insertOne(document);
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();
            log.info(CREATE_TRIP_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_TRIP, tripRecord, e);
            throw new DataAccessException(TRIP_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<TripRecord> getTripById(String id) throws DataAccessException{
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();
            if (result != null) {
                TripRecord tripRecord = fromDocument(result);
                log.info(GET_TRIP_START, id);
                return Optional.of(tripRecord);
            } else {
                log.warn(WARN_TRIP_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_TRIP, id, e);
            throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateTrip(TripRecord tripRecord) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(tripRecord.getId());
            Document update = toDocument(tripRecord);
            update.put(MONGO_ID, objectId);
            update.put(USER_ID, new ObjectId(tripRecord.getUserId()));
            update.put(ROUTE_ID, new ObjectId(tripRecord.getRouteId()));

            // Выполняем обновление и проверяем количество обновленных записей
            UpdateResult updateResult = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));

            if (updateResult.getModifiedCount() == 0) {
                log.warn(WARN_TRIP_NOT_FOUND, tripRecord.getId());
                throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, tripRecord.getId()));
            }
            log.info(UPDATE_TRIP_SUCCESS, tripRecord.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_TRIP, tripRecord.getId(), e);
            throw new DataAccessException(TRIP_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException{
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_TRIP_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_TRIP, id, e);
            throw new DataAccessException(TRIP_DELETE_ERROR, e);
        }
    }
}

