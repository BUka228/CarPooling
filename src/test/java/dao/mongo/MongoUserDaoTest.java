package dao.mongo;

import com.carpooling.dao.mongo.MongoUserDao;
import com.carpooling.entities.record.UserRecord;
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

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoUserDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoUserDao userDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDao = new MongoUserDao(collection);
    }

    @Test
    void testCreateUser_Success() throws JsonProcessingException {
        // Arrange
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");

        Document document = new Document("_id", new ObjectId())
                .append("name", userRecord.getName())
                .append("email", userRecord.getEmail());

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        // Act
        String userId = userDao.createUser(userRecord);

        // Assert
        assertNotNull(userId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateUser_Failure() throws JsonProcessingException {
        // Arrange
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> userDao.createUser(userRecord));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetUserById_Success() throws JsonProcessingException {
        // Arrange
        String userId = new ObjectId().toHexString();
        UserRecord userRecord = new UserRecord(
                userId,
                "John Doe",
                "john.doe@example.com",
                "password",
                "MALE",
                "1234567890",
                null,
                "Some address",
                null
        );

        Document document = toDocument(userRecord);
        document.put("_id", new ObjectId(userId));

        when(collection.find(eq(new ObjectId(userId)))).thenReturn(new FakeFindIterable(document));

        // Act
        Optional<UserRecord> user = userDao.getUserById(userId);

        // Assert
        assertTrue(user.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(userId)));
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        String userId = new ObjectId().toHexString();

        when(collection.find(eq(new ObjectId(userId)))).thenReturn(new FakeFindIterable(null));

        // Act
        Optional<UserRecord> user = userDao.getUserById(userId);

        // Assert
        assertFalse(user.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(userId)));
    }

    @Test
    void testUpdateUser_Success() throws JsonProcessingException {
        // Arrange
        UserRecord userRecord = new UserRecord(
                new ObjectId().toHexString(),
                "John Doe",
                "john.doe@example.com",
                "password",
                "MALE",
                "1234567890",
                null,
                "Some address",
                null
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        // Act
        userDao.updateUser(userRecord);

        // Assert
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateUser_Failure() throws JsonProcessingException {
        // Arrange
        UserRecord userRecord = new UserRecord(
                new ObjectId().toHexString(),
                "John Doe",
                "john.doe@example.com",
                "password",
                "MALE",
                "1234567890",
                null,
                "Some address",
                null
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> userDao.updateUser(userRecord));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        String userId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(userId)))).thenReturn(DeleteResult.acknowledged(1L));

        // Act
        userDao.deleteUser(userId);

        // Assert
        verify(collection, times(1)).deleteOne(eq(new ObjectId(userId)));
    }

    @Test
    void testDeleteUser_Failure() {
        // Arrange
        String userId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(userId)))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () -> userDao.deleteUser(userId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(userId)));
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


