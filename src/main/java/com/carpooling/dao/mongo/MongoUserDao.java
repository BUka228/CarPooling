package com.carpooling.dao.mongo;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
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
public class MongoUserDao extends AbstractMongoDao<User> implements UserDao {

    public MongoUserDao(MongoCollection<Document> collection) {
        super(collection, User.class);
    }

    @Override
    public String createUser(User user) throws DataAccessException {
        try {
            Document document = toDocument(user);
            collection.insertOne(document);

            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();
            log.info("User created successfully: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public Optional<User> getUserById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();
            if (result != null) {
                User user = fromDocument(result);
                log.info("User found: {}", id);
                return Optional.of(user);
            } else {
                log.warn("User not found: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error reading user: {}", e.getMessage());
            throw new DataAccessException("Error reading user", e);
        }
    }

    @Override
    public void updateUser(User user) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(user.getId().toString());
            Document update = toDocument(user);
            UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
            if (result.getModifiedCount() == 0) {
                log.warn("User not found for update: {}", user.getId());
                throw new DataAccessException("User not found");
            }
            log.info("User updated successfully: {}", user.getId());
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            throw new DataAccessException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
            if (result.getDeletedCount() > 0) {
                log.info("User deleted successfully: {}", id);
            } else {
                log.warn("User not found for deletion: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new DataAccessException("Error deleting user", e);
        }
    }
}
