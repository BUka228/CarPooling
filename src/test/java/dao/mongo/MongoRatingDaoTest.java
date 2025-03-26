package dao.mongo;

import com.carpooling.dao.mongo.MongoRatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoRatingDaoTest {

    @Mock
    private MongoCollection<Document> mockCollection;
    @Mock
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoRatingDao ratingDao;

    private Rating testRating;
    private Document testDocument;
    private String testRatingIdStr;
    private UUID testRatingIdUUID;

    @BeforeEach
    void setUp() {
        testRatingIdUUID = UUID.randomUUID();
        testRatingIdStr = testRatingIdUUID.toString();

        testRating = new Rating();
        // testRating.setId(testRatingIdUUID); // Устанавливается в createRating
        testRating.setRating(5);
        testRating.setComment("Excellent!");
        testRating.setDate(LocalDateTime.now());

        testDocument = new Document()
                .append("id", testRatingIdStr)
                .append("rating", 5)
                .append("comment", "Excellent!")
                .append("date", testRating.getDate());
    }

    // --- Тесты для createRating ---

    @Test
    void createRating_Success_ShouldReturnGeneratedId() {
        // Arrange
        Rating ratingToCreate = new Rating();
        ratingToCreate.setRating(4);
        ratingToCreate.setComment("Good");

        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            assertNotNull(doc.getString("id"), "ID должен быть установлен DAO");
            return null;
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = ratingDao.createRating(ratingToCreate);

        // Assert
        assertNotNull(createdId);
        assertNotNull(ratingToCreate.getId());
        assertEquals(createdId, ratingToCreate.getId().toString());

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());
        Document insertedDoc = docCaptor.getValue();
        assertEquals(ratingToCreate.getId().toString(), insertedDoc.getString("id"));
        assertEquals(4, insertedDoc.getInteger("rating"));
        assertEquals("Good", insertedDoc.getString("comment"));
    }

    @Test
    void createRating_Failure_ShouldThrowDataAccessException() {
        // Arrange
        Rating ratingToCreate = new Rating();
        ratingToCreate.setRating(1);

        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            ratingDao.createRating(ratingToCreate);
        });
        assertTrue(exception.getMessage().contains("Error creating rating"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertNotNull(ratingToCreate.getId()); // ID генерируется до ошибки
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getRatingById ---

    @Test
    void getRatingById_Found_ShouldReturnOptionalWithRating() {
        // Arrange
        when(mockCollection.find(eq(Filters.eq("id", testRatingIdStr)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument);

        // Act
        Optional<Rating> result = ratingDao.getRatingById(testRatingIdStr);

        // Assert
        assertTrue(result.isPresent());
        Rating foundRating = result.get();
        assertEquals(testRatingIdUUID, foundRating.getId());
        assertEquals(5, foundRating.getRating());
        assertEquals("Excellent!", foundRating.getComment());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", testRatingIdStr)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getRatingById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", nonExistentId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        // Act
        Optional<Rating> result = ratingDao.getRatingById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", nonExistentId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getRatingById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", id)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            ratingDao.getRatingById(id);
        });
        assertTrue(exception.getMessage().contains("Error reading rating"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", id)));
        verify(mockFindIterable, never()).first();
    }


    // --- Тесты для updateRating ---

    @Test
    void updateRating_Success_ShouldCallUpdateOne() {
        // Arrange
        testRating.setId(testRatingIdUUID);
        testRating.setComment("Updated Comment");
        String ratingIdString = testRatingIdUUID.toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq(Filters.eq("id", ratingIdString)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        assertDoesNotThrow(() -> ratingDao.updateRating(testRating));

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        assertEquals(Filters.eq("id", ratingIdString), filterCaptor.getValue());
        Document setDoc = updateCaptor.getValue().get("$set", Document.class);
        assertNotNull(setDoc);
        assertEquals(ratingIdString, setDoc.getString("id"));
        assertEquals("Updated Comment", setDoc.getString("comment"));
        assertEquals(5, setDoc.getInteger("rating")); // Убедимся, что другие поля тоже есть
    }

    @Test
    void updateRating_NotFound_ShouldThrowDataAccessException() {
        // Arrange
        testRating.setId(testRatingIdUUID);
        String id = testRating.getId().toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L);
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            ratingDao.updateRating(testRating);
        });
        // Проверяем точное сообщение согласно коду DAO
        assertEquals("Error updating rating", exception.getMessage());
        assertEquals("Rating not found", exception.getCause().getMessage());
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    @Test
    void updateRating_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testRating.setId(testRatingIdUUID);
        String id = testRating.getId().toString();

        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            ratingDao.updateRating(testRating);
        });
        assertTrue(exception.getMessage().contains("Error updating rating"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    // --- Тесты для deleteRating ---

    @Test
    void deleteRating_Success_ShouldCallDeleteOne() {
        // Arrange
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", testRatingIdStr))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> ratingDao.deleteRating(testRatingIdStr));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", testRatingIdStr)));
    }

    @Test
    void deleteRating_NotFound_ShouldCallDeleteOneAndNotThrow() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(0L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", nonExistentId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> ratingDao.deleteRating(nonExistentId));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", nonExistentId)));
    }

    @Test
    void deleteRating_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.deleteOne(eq(Filters.eq("id", id))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            ratingDao.deleteRating(id);
        });
        assertTrue(exception.getMessage().contains("Error deleting rating"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", id)));
    }
}