package com.carpooling.dao.mongo;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
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
public class MongoBookingDao extends AbstractMongoDao<Booking> implements BookingDao {

    public MongoBookingDao(MongoCollection<Document> collection) {
        super(collection, Booking.class);
    }

    @Override
    public String createBooking(Booking booking) throws DataAccessException {
        try {
            Document document = toDocument(booking);
            collection.insertOne(document);
            ObjectId generatedId = document.getObjectId("_id");
            String id = generatedId.toHexString();
            log.info("Booking created successfully: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw new DataAccessException("Error creating booking", e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq("_id", objectId)).first();
            if (result != null) {
                Booking booking = fromDocument(result);
                log.info("Booking found: {}", id);
                return Optional.of(booking);
            } else {
                log.warn("Booking not found: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error reading booking: {}", e.getMessage());
            throw new DataAccessException("Error reading booking", e);
        }
    }

    @Override
    public void updateBooking(Booking booking) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(booking.getId().toString());
            Document update = toDocument(booking);
            UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), new Document("$set", update));
            if (result.getModifiedCount() == 0) {
                log.warn("Booking not found for update: {}", booking.getId());
                throw new DataAccessException("Booking not found");
            }
            log.info("Booking updated successfully: {}", booking.getId());
        } catch (Exception e) {
            log.error("Error updating booking: {}", e.getMessage());
            throw new DataAccessException("Error updating booking", e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", objectId));
            if (result.getDeletedCount() > 0) {
                log.info("Booking deleted successfully: {}", id);
            } else {
                log.warn("Booking not found for deletion: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting booking: {}", e.getMessage());
            throw new DataAccessException("Error deleting booking", e);
        }
    }
}
