package dao.mongo;

import com.carpooling.dao.mongo.MongoHistoryContentDao;
import com.carpooling.entities.history.HistoryContent;
import com.carpooling.exceptions.dao.DataAccessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoHistoryContentDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoHistoryContentDao historyContentDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        historyContentDao = new MongoHistoryContentDao(collection);
    }

    @Test
    void testCreateHistory_Success() throws JsonProcessingException {
        
        HistoryContent historyContent = new HistoryContent();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        
        String historyId = historyContentDao.createHistory(historyContent);

        
        assertNotNull(historyId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateHistory_Failure() throws JsonProcessingException {
        
        HistoryContent historyContent = new HistoryContent();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> historyContentDao.createHistory(historyContent));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetHistoryById_Success() throws JsonProcessingException {
        
        String historyId = new ObjectId().toHexString();
        HistoryContent historyContent = new HistoryContent(
                historyId,
                "VIEW",
                LocalDateTime.now(),
                "user1",
                "VIEW",
                null,
                null
        );

        Document document = toDocument(historyContent);
        document.put("_id", new ObjectId(historyId));

        when(collection.find(eq(new ObjectId(historyId)))).thenReturn(new FakeFindIterable(document));

        
        Optional<HistoryContent> history = historyContentDao.getHistoryById(historyId);

        
        assertTrue(history.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(historyId)));
    }

    @Test
    void testGetHistoryById_NotFound() {
        
        String historyId = new ObjectId().toHexString();

        when(collection.find(eq(new ObjectId(historyId)))).thenReturn(new FakeFindIterable(null));

        
        Optional<HistoryContent> history = historyContentDao.getHistoryById(historyId);

        
        assertFalse(history.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(historyId)));
    }

    @Test
    void testUpdateHistory_Success() throws JsonProcessingException {
        
        HistoryContent historyContent = new HistoryContent(
                new ObjectId().toHexString(),
                "VIEW",
                LocalDateTime.now(),
                "user1",
                "VIEW",
                null,
                null
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        
        historyContentDao.updateHistory(historyContent);

        
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateHistory_Failure() throws JsonProcessingException {
        HistoryContent historyContent = new HistoryContent(
                new ObjectId().toHexString(),
                "VIEW",
                LocalDateTime.now(),
                "user1",
                "VIEW",
                null,
                null
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> historyContentDao.updateHistory(historyContent));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteHistory_Success() {
        
        String historyId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(historyId)))).thenReturn(DeleteResult.acknowledged(1L));

        
        historyContentDao.deleteHistory(historyId);

        
        verify(collection, times(1)).deleteOne(eq(new ObjectId(historyId)));
    }

    @Test
    void testDeleteHistory_Failure() {
        
        String historyId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(historyId)))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> historyContentDao.deleteHistory(historyId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(historyId)));
    }

    protected Document toDocument(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для поддержки Java 8 date/time
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