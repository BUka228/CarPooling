package data.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import data.dao.base.RatingDao;
import data.model.record.RatingRecord;
import exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.man.constant.Constants.MONGO_ID;
import static com.man.constant.Constants.TRIP_ID;
import static com.man.constant.ErrorMessages.RATING_CREATION_ERROR;
import static com.man.constant.ErrorMessages.RATING_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;

@Slf4j
public class MongoRatingDao extends AbstractMongoDao<RatingRecord> implements RatingDao {

    

    public MongoRatingDao(MongoCollection<Document> collection) {
        super(collection, RatingRecord.class);
    }

    @Override
    public String createRating(RatingRecord ratingRecord) throws DataAccessException {
        try {
            // Преобразуем объект Rating в документ для MongoDB
            Document document = toDocument(ratingRecord);
            document.put(TRIP_ID, new ObjectId(ratingRecord.getTripId()));
            collection.insertOne(document);

            // Получаем сгенерированный ObjectId и возвращаем его как строку
            ObjectId generatedId = document.getObjectId(MONGO_ID);
            String id = generatedId.toHexString();

            log.info(CREATE_RATING_SUCCESS, id);
            return id;
        } catch (Exception e) {
            log.error(ERROR_CREATE_RATING, ratingRecord, e);
            throw new DataAccessException(RATING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RatingRecord> getRatingById(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            Document result = collection.find(Filters.eq(MONGO_ID, objectId)).first();

            if (result != null) {
                result.remove(TRIP_ID);
                // Преобразуем документ обратно в объект RatingRecord
                RatingRecord ratingRecord = fromDocument(result);
                log.info(GET_RATING_START, id);
                return Optional.of(ratingRecord);
            } else {
                log.warn(WARN_RATING_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(ERROR_GET_RATING, id, e);
            throw new DataAccessException(RATING_NOT_FOUND_ERROR, e);
        }
    }

    @Override
    public void updateRating(RatingRecord ratingRecord) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(ratingRecord.getId());
            Document update = toDocument(ratingRecord);
            update.put(MONGO_ID, objectId);
            update.put(TRIP_ID, new ObjectId(ratingRecord.getTripId()));

            collection.updateOne(Filters.eq(MONGO_ID, objectId), new Document("$set", update));
            log.info(UPDATE_RATING_SUCCESS, ratingRecord.getId());
        } catch (Exception e) {
            log.error(ERROR_UPDATE_RATING, ratingRecord.getId(), e);
            throw new DataAccessException(RATING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            ObjectId objectId = new ObjectId(id);
            collection.deleteOne(Filters.eq(MONGO_ID, objectId));
            log.info(DELETE_RATING_SUCCESS, id);
        } catch (Exception e) {
            log.error(ERROR_DELETE_RATING, id, e);
            throw new DataAccessException(RATING_DELETE_ERROR, e);
        }
    }
}

