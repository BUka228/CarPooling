package com.carpooling.dao.mongo;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.record.UserRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.carpooling.constants.Constants.MONGO_ID;
import static com.carpooling.constants.ErrorMessages.USER_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class MongoUserDao extends AbstractMongoDao<UserRecord> implements UserDao {

    public MongoUserDao(MongoCollection<Document> collection) {
        super(collection, UserRecord.class);
    }

    @Override
    public String createUser(UserRecord userRecord) throws DataAccessException{
        try {
            Document document = toDocument(userRecord);
            collection.insertOne(document);
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();

            log.info(CREATE_USER_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_USER, userRecord, e);
            throw new DataAccessException(USER_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<UserRecord> getUserById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();
            if (result != null) {
                UserRecord userRecord = fromDocument(result);
                log.info(GET_USER_START, id);
                return Optional.of(userRecord);
            } else {
                log.warn(WARN_USER_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_USER, id, e);
            throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateUser(UserRecord userRecord) {
        try {
            ObjectId objectId = new ObjectId(userRecord.getId());
            Document update = toDocument(userRecord);
            update.put(MONGO_ID, objectId);

            collection.updateOne(Filters.eq(MONGO_ID, objectId), new Document("$set", update));
            log.info(UPDATE_USER_SUCCESS, userRecord.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_USER, userRecord.getId(), e);
            throw new DataAccessException(USER_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteUser(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_USER_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_USER, id, e);
            throw new DataAccessException(USER_DELETE_ERROR, e);
        }
    }
}


