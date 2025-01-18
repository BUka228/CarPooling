package dao.mongo;

import com.carpooling.dao.mongo.MongoTripDao;
import com.carpooling.entities.record.TripRecord;
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

class MongoTripDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoTripDao tripDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tripDao = new MongoTripDao(collection);
    }

    @Test
    void testCreateTrip_Success() throws JsonProcessingException {
        // Arrange
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId(new ObjectId().toHexString());
        tripRecord.setRouteId(new ObjectId().toHexString());

        Document document = new Document("_id", new ObjectId())
                .append("userId", new ObjectId(tripRecord.getUserId()))
                .append("routeId", new ObjectId(tripRecord.getRouteId()));

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        // Act
        String tripId = tripDao.createTrip(tripRecord);

        // Assert
        assertNotNull(tripId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateTrip_Failure() throws JsonProcessingException {
        // Arrange
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId(new ObjectId().toHexString());
        tripRecord.setRouteId(new ObjectId().toHexString());

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.createTrip(tripRecord));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetTripById_Success() throws JsonProcessingException {
        // Arrange
        String tripId = new ObjectId().toHexString();
        TripRecord tripRecord = new TripRecord(
                tripId,
                new Date(),
                (byte) 4,
                new Date(),
                "SCHEDULED",
                true,
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );

        Document document = toDocument(tripRecord);
        document.put("_id", new ObjectId(tripId));


        when(collection.find(eq(new ObjectId(tripId)))).thenReturn(new FakeFindIterable(document));


        // Act
        Optional<TripRecord> trip = tripDao.getTripById(tripId);

        // Assert
        assertTrue(trip.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(tripId)));
    }

    @Test
    void testGetTripById_NotFound() {
        // Arrange
        String tripId = "507f1f77bcf86cd799439011";

        when(collection.find(eq(new ObjectId(tripId)))).thenReturn(new FakeFindIterable(null));

        // Act
        Optional<TripRecord> trip = tripDao.getTripById(tripId);

        // Assert
        assertFalse(trip.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(tripId)));
    }

    @Test
    void testUpdateTrip_Success() throws JsonProcessingException {
        // Arrange
        TripRecord tripRecord = new TripRecord(
                new ObjectId().toHexString(),
                new Date(),
                (byte) 4,
                new Date(),
                "SCHEDULED",
                true,
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );



        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        // Act
        tripDao.updateTrip(tripRecord);

        // Assert
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateTrip_Failure() throws JsonProcessingException {
        // Arrange
        TripRecord tripRecord = new TripRecord(
                new ObjectId().toHexString(),
                new Date(),
                (byte) 4,
                new Date(),
                "SCHEDULED",
                true,
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.updateTrip(tripRecord));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteTrip_Success() {
        // Arrange
        String tripId = "507f1f77bcf86cd799439011";

        when(collection.deleteOne(eq(new ObjectId(tripId)))).thenReturn(DeleteResult.acknowledged(1L));

        // Act
        tripDao.deleteTrip(tripId);

        // Assert
        verify(collection, times(1)).deleteOne(eq(new ObjectId(tripId)));
    }

    @Test
    void testDeleteTrip_Failure() {
        // Arrange
        String tripId = "507f1f77bcf86cd799439011";

        when(collection.deleteOne(eq(new ObjectId(tripId)))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip(tripId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(tripId)));
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