package data.dao.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import data.dao.fake.FakeFindIterable;
import data.model.database.Route;
import data.model.record.RouteRecord;
import exceptions.dao.DataAccessException;
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

class MongoRouteDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRouteDao routeDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeDao = new MongoRouteDao(collection);
    }

    @Test
    void testCreateRoute_Success() throws JsonProcessingException {
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        Document document = new Document("_id", new ObjectId())
                .append("startPoint", routeRecord.getStartPoint())
                .append("endPoint", routeRecord.getEndPoint())
                .append("date", routeRecord.getDate())
                .append("estimatedDuration", routeRecord.getEstimatedDuration());

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        String routeId = routeDao.createRoute(routeRecord);

        assertNotNull(routeId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateRoute_Failure() throws JsonProcessingException {

        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> routeDao.createRoute(routeRecord));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetRouteById_Success() throws JsonProcessingException {
        String routeId = new ObjectId().toHexString();
        RouteRecord routeRecord = new RouteRecord(
                routeId,
                "Start Point",
                "End Point",
                new Date(),
                (short) 120
        );

        Document document = toDocument(routeRecord);
        document.put("_id", new ObjectId(routeId));

        when(collection.find(eq(new ObjectId(routeId)))).thenReturn(new FakeFindIterable(document));

        Optional<RouteRecord> route = routeDao.getRouteById(routeId);

        assertTrue(route.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(routeId)));
    }

    @Test
    void testGetRouteById_NotFound() {
        // Arrange
        String routeId = new ObjectId().toHexString();

        when(collection.find(eq(new ObjectId(routeId)))).thenReturn(new FakeFindIterable(null));

        Optional<RouteRecord> route = routeDao.getRouteById(routeId);

        assertFalse(route.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(routeId)));
    }

    @Test
    void testUpdateRoute_Success() throws JsonProcessingException {
        RouteRecord routeRecord = new RouteRecord(
                new ObjectId().toHexString(),
                "Start Point",
                "End Point",
                new Date(),
                (short) 120
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        routeDao.updateRoute(routeRecord);

        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateRoute_Failure() throws JsonProcessingException {
        RouteRecord routeRecord = new RouteRecord(
                new ObjectId().toHexString(),
                "Start Point",
                "End Point",
                new Date(),
                (short) 120
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> routeDao.updateRoute(routeRecord));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteRoute_Success() {
        String routeId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(routeId)))).thenReturn(DeleteResult.acknowledged(1L));

        routeDao.deleteRoute(routeId);

        verify(collection, times(1)).deleteOne(eq(new ObjectId(routeId)));
    }

    @Test
    void testDeleteRoute_Failure() {
        String routeId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(routeId)))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> routeDao.deleteRoute(routeId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(routeId)));
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