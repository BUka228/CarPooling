package dao.mongo;

import com.carpooling.dao.mongo.MongoHistoryContentDao;
import com.carpooling.entities.history.HistoryContent;
import com.carpooling.entities.enums.Status;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult; // Не используется для проверки, но нужен для when
import com.mongodb.client.result.UpdateResult; // Не используется для проверки, но нужен для when
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId; // Важно
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoHistoryContentDaoTest {

    @Mock
    private MongoCollection<Document> mockCollection;
    @Mock
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoHistoryContentDao historyContentDao;

    private HistoryContent testHistoryContent;
    private Document testDocument;
    private ObjectId testObjectId;
    private String testObjectIdStr;

    @BeforeEach
    void setUp() {
        testObjectId = new ObjectId();
        testObjectIdStr = testObjectId.toHexString();

        testHistoryContent = new HistoryContent();
        testHistoryContent.setId(testObjectIdStr);
        testHistoryContent.setCreatedDate(LocalDateTime.now());
        testHistoryContent.setStatus(Status.FAULT);
        // Добавьте другие поля HistoryContent

        // Документ, как он хранится в Mongo (с _id типа ObjectId)
        testDocument = new Document()
                .append("_id", testObjectId) // Используем ObjectId
                .append("createdDate", testHistoryContent.getCreatedDate())
                .append("status", testHistoryContent.getStatus());
    }

    // --- Тесты для createHistory ---

    @Test
    void createHistory_Success_ShouldReturnGeneratedId() {
        // Arrange
        HistoryContent historyToCreate = new HistoryContent();
        historyToCreate.setStatus(Status.FAULT);
        ObjectId generatedObjectId = new ObjectId();

        // Мокаем insertOne - симулируем добавление _id базой данных
        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            // Убедимся, что наш DAO не пытается записать строковый ID (он ожидает генерации _id)
            assertNull(doc.get("id"));
            assertNull(doc.get("_id")); // Перед insertOne _id тоже не должно быть
            // Симулируем, что MongoDB добавил _id
            doc.put("_id", generatedObjectId);
            return null; // insertOne возвращает void
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = historyContentDao.createHistory(historyToCreate);

        // Assert
        assertNotNull(createdId, "Возвращенный ID не должен быть null");
        assertEquals(generatedObjectId.toHexString(), createdId, "Возвращенный ID должен совпадать с 'сгенерированным'");
        // ID в самом объекте historyToCreate не устанавливается этим методом

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());

        Document insertedDoc = docCaptor.getValue();
        // Проверяем, что _id был добавлен (хотя его добавляет мок `doAnswer`)
        assertEquals(generatedObjectId, insertedDoc.getObjectId("_id"));
    }

    @Test
    void createHistory_Failure_ShouldThrowDataAccessException() {
        // Arrange
        HistoryContent historyToCreate = new HistoryContent();
        historyToCreate.setStatus(Status.FAULT);

        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            historyContentDao.createHistory(historyToCreate);
        });

        assertTrue(exception.getMessage().contains("Error creating history"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("DB connection error", exception.getCause().getMessage());

        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getHistoryById ---

    @Test
    void getHistoryById_Found_ShouldReturnOptionalWithHistory() {
        // Arrange
        // Фильтр теперь по _id и ObjectId
        when(mockCollection.find(eq(Filters.eq("_id", testObjectId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument);

        // Act
        Optional<HistoryContent> result = historyContentDao.getHistoryById(testObjectIdStr);

        // Assert
        assertTrue(result.isPresent(), "Optional должен содержать значение");
        HistoryContent foundHistory = result.get();
        // AbstractMongoDao.fromDocument должен преобразовать _id в строковый id
        assertEquals(testObjectIdStr, foundHistory.getId(), "ID найденной истории не совпадает");
        assertEquals(testHistoryContent.getStatus(), foundHistory.getStatus(), "Детали не совпадают");
        // ... другие проверки полей

        verify(mockCollection, times(1)).find(eq(Filters.eq("_id", testObjectId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getHistoryById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        ObjectId nonExistentObjectId = new ObjectId();
        String nonExistentIdStr = nonExistentObjectId.toHexString();
        when(mockCollection.find(eq(Filters.eq("_id", nonExistentObjectId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        // Act
        Optional<HistoryContent> result = historyContentDao.getHistoryById(nonExistentIdStr);

        // Assert
        assertTrue(result.isEmpty(), "Optional должен быть пустым");

        verify(mockCollection, times(1)).find(eq(Filters.eq("_id", nonExistentObjectId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getHistoryById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        ObjectId objectId = new ObjectId();
        String id = objectId.toHexString();
        when(mockCollection.find(eq(Filters.eq("_id", objectId)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            historyContentDao.getHistoryById(id);
        });
        // Сообщение из DAO отличается
        assertEquals("History not found", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Query failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).find(eq(Filters.eq("_id", objectId)));
        verify(mockFindIterable, never()).first();
    }

    // --- Тесты для updateHistory ---

    @Test
    void updateHistory_Success_ShouldCallUpdateOne() {
        // Arrange
        testHistoryContent.setStatus(Status.FAULT);
        // Сам DAO добавляет _id в $set
        Document expectedSetDocument = new Document()
                .append("_id", testObjectId) // DAO добавляет _id
                .append("timestamp", testHistoryContent.getCreatedDate())
                .append("details", "Updated Details");
        Document expectedUpdate = new Document("$set", expectedSetDocument);

        // DAO не проверяет результат, так что мокаем простой UpdateResult
        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockCollection.updateOne(eq(Filters.eq("_id", testObjectId)), any(Document.class)))
                .thenReturn(mockUpdateResult); // Просто возвращаем мок

        // Act & Assert
        assertDoesNotThrow(() -> {
            historyContentDao.updateHistory(testHistoryContent);
        });

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        assertEquals(Filters.eq("_id", testObjectId), filterCaptor.getValue());

        // Проверка документа обновления ($set)
        Document updateDoc = updateCaptor.getValue();
        assertTrue(updateDoc.containsKey("$set"));
        Document setDoc = updateDoc.get("$set", Document.class);
        assertNotNull(setDoc);
        assertEquals(testObjectId, setDoc.getObjectId("_id"));
    }

    @Test
    void updateHistory_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testHistoryContent.setStatus(Status.FAULT);

        when(mockCollection.updateOne(eq(Filters.eq("_id", testObjectId)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            historyContentDao.updateHistory(testHistoryContent);
        });

        assertTrue(exception.getMessage().contains("Error updating history"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Update failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("_id", testObjectId)), any(Document.class));
    }

    // --- Тесты для deleteHistory ---

    @Test
    void deleteHistory_Success_ShouldCallDeleteOne() {
        // Arrange
        // DAO не проверяет результат, мокаем простой DeleteResult
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockCollection.deleteOne(eq(Filters.eq("_id", testObjectId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> {
            historyContentDao.deleteHistory(testObjectIdStr);
        });

        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("_id", testObjectId)));
    }

    @Test
    void deleteHistory_Failure_ShouldThrowDataAccessException() {
        // Arrange
        ObjectId objectId = new ObjectId();
        String id = objectId.toHexString();

        when(mockCollection.deleteOne(eq(Filters.eq("_id", objectId))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            historyContentDao.deleteHistory(id);
        });

        assertTrue(exception.getMessage().contains("Error deleting history"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Deletion failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("_id", objectId)));
    }
}