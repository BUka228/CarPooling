package dao.mongo;

import com.carpooling.dao.mongo.MongoTripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.enums.TripStatus;
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
class MongoTripDaoTest {

    @Mock
    private MongoCollection<Document> mockCollection;
    @Mock
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoTripDao tripDao;

    private Trip testTrip;
    private Document testDocument;
    private String testTripIdStr;
    private UUID testTripIdUUID;

    @BeforeEach
    void setUp() {
        testTripIdUUID = UUID.randomUUID();
        testTripIdStr = testTripIdUUID.toString();

        testTrip = new Trip();
        // testTrip.setId(testTripIdUUID); // Устанавливается в createTrip
        testTrip.setDepartureTime(LocalDateTime.now().plusHours(1));
        testTrip.setMaxPassengers((byte) 4);
        testTrip.setCreationDate(LocalDateTime.now());
        testTrip.setStatus(TripStatus.PLANNED);
        testTrip.setEditable(true);
        // testTrip.setUser(new User());   // Опционально
        // testTrip.setRoute(new Route()); // Опционально

        testDocument = new Document()
                .append("id", testTripIdStr)
                .append("departureTime", testTrip.getDepartureTime())
                .append("maxPassengers", (byte) 4)
                .append("creationDate", testTrip.getCreationDate())
                .append("status", TripStatus.PLANNED.name())
                .append("editable", true);

    }

    // --- Тесты для createTrip ---

    @Test
    void createTrip_Success_ShouldReturnGeneratedId() {
        // Arrange
        Trip tripToCreate = new Trip();
        tripToCreate.setStatus(TripStatus.ACTIVE);
        tripToCreate.setMaxPassengers((byte) 2);

        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            assertNotNull(doc.getString("id"));
            return null;
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = tripDao.createTrip(tripToCreate);

        // Assert
        assertNotNull(createdId);
        assertNotNull(tripToCreate.getId());
        assertEquals(createdId, tripToCreate.getId().toString());

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());
        Document insertedDoc = docCaptor.getValue();
        assertEquals(tripToCreate.getId().toString(), insertedDoc.getString("id"));
        assertEquals(TripStatus.ACTIVE.name(), insertedDoc.getString("status"));
    }

    @Test
    void createTrip_Failure_ShouldThrowDataAccessException() {
        // Arrange
        Trip tripToCreate = new Trip();
        tripToCreate.setStatus(TripStatus.CANCELLED);

        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            tripDao.createTrip(tripToCreate);
        });
        assertTrue(exception.getMessage().contains("Error creating trip"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertNotNull(tripToCreate.getId());
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getTripById ---

    @Test
    void getTripById_Found_ShouldReturnOptionalWithTrip() {
        // Arrange
        when(mockCollection.find(eq(Filters.eq("id", testTripIdStr)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument);

        // Act
        Optional<Trip> result = tripDao.getTripById(testTripIdStr);

        // Assert
        assertTrue(result.isPresent());
        Trip foundTrip = result.get();
        assertEquals(testTripIdUUID, foundTrip.getId());
        assertEquals(TripStatus.PLANNED, foundTrip.getStatus());
        assertEquals((byte) 4, foundTrip.getMaxPassengers());
        assertTrue(foundTrip.isEditable());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", testTripIdStr)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getTripById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", nonExistentId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        // Act
        Optional<Trip> result = tripDao.getTripById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", nonExistentId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getTripById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", id)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            tripDao.getTripById(id);
        });
        assertTrue(exception.getMessage().contains("Error reading trip"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", id)));
        verify(mockFindIterable, never()).first();
    }


    // --- Тесты для updateTrip ---

    @Test
    void updateTrip_Success_ShouldCallUpdateOne() {
        // Arrange
        testTrip.setId(testTripIdUUID);
        testTrip.setStatus(TripStatus.COMPLETED);
        testTrip.setEditable(false);
        String tripIdString = testTripIdUUID.toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq(Filters.eq("id", tripIdString)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        assertDoesNotThrow(() -> tripDao.updateTrip(testTrip));

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        assertEquals(Filters.eq("id", tripIdString), filterCaptor.getValue());
        Document setDoc = updateCaptor.getValue().get("$set", Document.class);
        assertNotNull(setDoc);
        assertEquals(tripIdString, setDoc.getString("id"));
        assertEquals("COMPLETED", setDoc.getString("status"));
        assertFalse(setDoc.getBoolean("editable"));
    }

    @Test
    void updateTrip_NotFound_ShouldThrowDataAccessException() {
        // Arrange
        testTrip.setId(testTripIdUUID);
        String id = testTrip.getId().toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L);
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            tripDao.updateTrip(testTrip);
        });
        assertEquals("Error updating trip", exception.getMessage());
        assertEquals("Trip not found", exception.getCause().getMessage());
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    @Test
    void updateTrip_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testTrip.setId(testTripIdUUID);
        String id = testTrip.getId().toString();

        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            tripDao.updateTrip(testTrip);
        });
        assertTrue(exception.getMessage().contains("Error updating trip"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    // --- Тесты для deleteTrip ---

    @Test
    void deleteTrip_Success_ShouldCallDeleteOne() {
        // Arrange
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", testTripIdStr))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> tripDao.deleteTrip(testTripIdStr));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", testTripIdStr)));
    }

    @Test
    void deleteTrip_NotFound_ShouldCallDeleteOneAndNotThrow() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(0L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", nonExistentId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> tripDao.deleteTrip(nonExistentId));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", nonExistentId)));
    }

    @Test
    void deleteTrip_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.deleteOne(eq(Filters.eq("id", id))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            tripDao.deleteTrip(id);
        });
        assertTrue(exception.getMessage().contains("Error deleting trip"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", id)));
    }
}