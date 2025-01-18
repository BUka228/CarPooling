package dao.mongo;

import com.carpooling.dao.mongo.MongoRatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dao.fake.FakeFindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoRatingDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRatingDao ratingDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ratingDao = new MongoRatingDao(collection);
    }

    @Test
    void testCreateRating_Success() throws JsonProcessingException {
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setTripId(new ObjectId().toHexString());
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");

        Document document = new Document("_id", new ObjectId())
                .append("tripId", new ObjectId(ratingRecord.getTripId()))
                .append("rating", ratingRecord.getRating())
                .append("comment", ratingRecord.getComment());

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        String ratingId = ratingDao.createRating(ratingRecord);

        assertNotNull(ratingId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateRating_Failure() throws JsonProcessingException {
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setTripId(new ObjectId().toHexString());
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> ratingDao.createRating(ratingRecord));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetRatingById_Success() throws JsonProcessingException {
        String ratingId = new ObjectId().toHexString();
        RatingRecord ratingRecord = new RatingRecord(
                ratingId,
                5,
                "Great trip!",
                new Date(),
                new ObjectId().toHexString()
        );

        Document document = toDocument(ratingRecord);
        document.put("_id", new ObjectId(ratingId));

        when(collection.find(eq(new ObjectId(ratingId)))).thenReturn(new FakeFindIterable(document));

        Optional<RatingRecord> rating = ratingDao.getRatingById(ratingId);

        assertTrue(rating.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(ratingId)));
    }

    @Test
    void testGetRatingById_NotFound() {
        String ratingId = new ObjectId().toHexString();

        when(collection.find(eq(new ObjectId(ratingId)))).thenReturn(new FakeFindIterable(null));

        Optional<RatingRecord> rating = ratingDao.getRatingById(ratingId);

        assertFalse(rating.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(ratingId)));
    }

    @Test
    void testUpdateRating_Success() throws JsonProcessingException {
        RatingRecord ratingRecord = new RatingRecord(
                new ObjectId().toHexString(),
                5,
                "Great trip!",
                new Date(),
                new ObjectId().toHexString()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        ratingDao.updateRating(ratingRecord);

        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateRating_Failure() throws JsonProcessingException {
        // Arrange
        RatingRecord ratingRecord = new RatingRecord(
                new ObjectId().toHexString(),
                5,
                "Great trip!",
                new Date(),
                new ObjectId().toHexString()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(ratingRecord));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteRating_Success() {
        // Arrange
        String ratingId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(ratingId)))).thenReturn(DeleteResult.acknowledged(1L));

        // Act
        ratingDao.deleteRating(ratingId);

        // Assert
        verify(collection, times(1)).deleteOne(eq(new ObjectId(ratingId)));
    }

    @Test
    void testDeleteRating_Failure() {
        // Arrange
        String ratingId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(ratingId)))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating(ratingId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(ratingId)));
    }

    protected Document toDocument(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(object);
            Document document = Document.parse(json);
            document.remove("id");
            return document;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось преобразовать объект в документ.", e);
        }
    }

    protected <T> T fromDocument(Document document, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Убираем _id, заменяем его на id
            if (document.containsKey("_id")) {
                document.put("id", document.getObjectId("_id").toHexString());
                document.remove("_id");
            }
            String json = document.toJson();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось преобразовать документ в объект.", e);
        }
    }
}
