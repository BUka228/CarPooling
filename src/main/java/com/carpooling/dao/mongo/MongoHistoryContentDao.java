package com.carpooling.dao.mongo;

import com.carpooling.dao.base.HistoryContentDao;
import com.carpooling.entities.history.HistoryContent;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.carpooling.constants.Constants.MONGO_ID;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class MongoHistoryContentDao extends AbstractMongoDao<HistoryContent> implements HistoryContentDao {

    public MongoHistoryContentDao(MongoCollection<Document> collection) {
        super(collection, HistoryContent.class);
    }

    @Override
    public String createHistory(HistoryContent historyContent) throws DataAccessException {
        try {
            // Преобразуем объект HistoryContent в документ для MongoDB
            Document document = toDocument(historyContent);
            collection.insertOne(document);

            // Получаем сгенерированный ObjectId и возвращаем его как строку
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();

            log.info(CREATE_HISTORY_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_HISTORY, historyContent, e);
            throw new DataAccessException(HISTORY_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<HistoryContent> getHistoryById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();

            if (result != null) {
                // Преобразуем документ обратно в объект HistoryContent
                HistoryContent historyContent = fromDocument(result);
                log.info(GET_HISTORY_SUCCESS, id);
                return Optional.of(historyContent);
            } else {
                log.warn(WARN_HISTORY_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_HISTORY, id, e);
            throw new DataAccessException(HISTORY_NOT_FOUND_ERROR, e);
        }
    }

    @Override
    public void updateHistory(HistoryContent historyContent) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(historyContent.getId());
            Document update = toDocument(historyContent);
            update.put(MONGO_ID, objectId);

            collection.updateOne(Filters.eq(MONGO_ID, objectId), new Document("$set", update));
            log.info(UPDATE_HISTORY_SUCCESS, historyContent.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_HISTORY, historyContent.getId(), e);
            throw new DataAccessException(HISTORY_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteHistory(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_HISTORY_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_HISTORY, id, e);
            throw new DataAccessException(HISTORY_DELETE_ERROR, e);
        }
    }
}

