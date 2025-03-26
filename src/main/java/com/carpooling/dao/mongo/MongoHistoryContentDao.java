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


@Slf4j
public class MongoHistoryContentDao extends AbstractMongoDao<HistoryContent> implements HistoryContentDao {

    public MongoHistoryContentDao(MongoCollection<Document> collection) {
        super(collection, HistoryContent.class);
    }

    @Override
    public String createHistory(HistoryContent historyContent) throws DataAccessException {
        try {
            Document document = toDocument(historyContent);
            collection.insertOne(document);
            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();

            log.info("History created successfully with id: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating history: {}", historyContent, e);
            throw new DataAccessException("Error creating history", e);
        }
    }

    @Override
    public Optional<HistoryContent> getHistoryById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();

            if (result != null) {
                HistoryContent historyContent = fromDocument(result);
                log.info("History retrieved successfully with id: {}", id);
                return Optional.of(historyContent);
            } else {
                log.warn("History not found with id: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error getting history with id: {}", id, e);
            throw new DataAccessException("History not found", e);
        }
    }

    @Override
    public void updateHistory(HistoryContent historyContent) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(historyContent.getId());
            Document update = toDocument(historyContent);
            update.put("_id", objectId);

            collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
            log.info("History updated successfully with id: {}", historyContent.getId());
        } catch (Exception e) {
            log.error("Error updating history with id: {}", historyContent.getId(), e);
            throw new DataAccessException("Error updating history", e);
        }
    }

    @Override
    public void deleteHistory(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq("_id", objectId));
            log.info("History deleted successfully with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting history with id: {}", id, e);
            throw new DataAccessException("Error deleting history", e);
        }
    }
}
