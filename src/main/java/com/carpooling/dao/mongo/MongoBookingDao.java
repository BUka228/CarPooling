package com.carpooling.dao.mongo;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.record.BookingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.carpooling.constants.Constants.*;
import static com.carpooling.constants.ErrorMessages.BOOKING_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.BOOKING_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class MongoBookingDao extends AbstractMongoDao<BookingRecord> implements BookingDao {
    

    public MongoBookingDao(MongoCollection<Document> collection) {
        super(collection, BookingRecord.class);
    }

    @Override
    public String createBooking(BookingRecord bookingRecord) throws DataAccessException {
        try {
            // Преобразуем объект Booking в документ для MongoDB
            Document document = toDocument(bookingRecord);
            document.put(TRIP_ID, new ObjectId(bookingRecord.getTripId()));
            document.put(USER_ID, new ObjectId(bookingRecord.getUserId()));

            // Вставляем документ в коллекцию
            collection.insertOne(document);

            // Получаем сгенерированный ObjectId и возвращаем его как строку
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();

            log.info(CREATE_BOOKING_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_BOOKING, bookingRecord, e);
            throw new DataAccessException(BOOKING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<BookingRecord> getBookingById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();
            if (result != null) {
                BookingRecord bookingRecord = fromDocument(result);
                log.info(GET_BOOKING_START, id);
                return Optional.of(bookingRecord);
            } else {
                log.warn(WARN_BOOKING_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_BOOKING, id, e);
            throw new DataAccessException(BOOKING_NOT_FOUND_ERROR, e);
        }
    }

    @Override
    public void updateBooking(BookingRecord bookingRecord) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(bookingRecord.getId());
            Document update = toDocument(bookingRecord);
            update.put(MONGO_ID, objectId);

            collection.updateOne(Filters.eq(MONGO_ID, objectId), new Document("$set", update));
            log.info(UPDATE_BOOKING_SUCCESS, bookingRecord.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_BOOKING, bookingRecord.getId(), e);
            throw new DataAccessException(BOOKING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_BOOKING_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_BOOKING, id, e);
            throw new DataAccessException(BOOKING_DELETE_ERROR, e);
        }
    }
}
