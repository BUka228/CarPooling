package dao.mongo;

import com.carpooling.dao.mongo.MongoRouteDao;
import com.carpooling.entities.database.Route;
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
class MongoRouteDaoTest {

    @Mock
    private MongoCollection<Document> mockCollection;
    @Mock
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoRouteDao routeDao;

    private Route testRoute;
    private Document testDocument;
    private String testRouteIdStr;
    private UUID testRouteIdUUID;

    @BeforeEach
    void setUp() {
        testRouteIdUUID = UUID.randomUUID();
        testRouteIdStr = testRouteIdUUID.toString();

        testRoute = new Route();
        // testRoute.setId(testRouteIdUUID); // Устанавливается в createRoute
        testRoute.setStartingPoint("Start");
        testRoute.setEndingPoint("End");
        testRoute.setDate(LocalDateTime.now());
        testRoute.setEstimatedDuration((short) 120);

        testDocument = new Document()
                .append("id", testRouteIdStr)
                .append("startingPoint", "Start")
                .append("endingPoint", "End")
                .append("date", testRoute.getDate())
                .append("estimatedDuration", (short) 120);
    }

    // --- Тесты для createRoute ---

    @Test
    void createRoute_Success_ShouldReturnGeneratedId() {
        // Arrange
        Route routeToCreate = new Route();
        routeToCreate.setStartingPoint("New Start");

        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            assertNotNull(doc.getString("id")); // ID должен быть установлен DAO
            return null;
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = routeDao.createRoute(routeToCreate);

        // Assert
        assertNotNull(createdId);
        assertNotNull(routeToCreate.getId());
        assertEquals(createdId, routeToCreate.getId().toString());

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());
        assertEquals(routeToCreate.getId().toString(), docCaptor.getValue().getString("id"));
        assertEquals("New Start", docCaptor.getValue().getString("startingPoint"));
    }

    @Test
    void createRoute_Failure_ShouldThrowDataAccessException() {
        // Arrange
        Route routeToCreate = new Route();
        routeToCreate.setStartingPoint("Fail Start");

        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            routeDao.createRoute(routeToCreate);
        });
        assertTrue(exception.getMessage().contains("Error creating route"));
        assertNotNull(exception.getCause());
        assertInstanceOf(MongoException.class, exception.getCause());
        assertNotNull(routeToCreate.getId()); // ID генерируется до ошибки
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getRouteById ---

    @Test
    void getRouteById_Found_ShouldReturnOptionalWithRoute() {
        // Arrange
        when(mockCollection.find(eq(Filters.eq("id", testRouteIdStr)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument);

        // Act
        Optional<Route> result = routeDao.getRouteById(testRouteIdStr);

        // Assert
        assertTrue(result.isPresent());
        Route foundRoute = result.get();
        assertEquals(testRouteIdUUID, foundRoute.getId());
        assertEquals("Start", foundRoute.getStartingPoint());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", testRouteIdStr)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getRouteById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", nonExistentId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        // Act
        Optional<Route> result = routeDao.getRouteById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", nonExistentId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getRouteById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", id)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            routeDao.getRouteById(id);
        });
        assertTrue(exception.getMessage().contains("Error reading route"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", id)));
        verify(mockFindIterable, never()).first();
    }


    // --- Тесты для updateRoute ---

    @Test
    void updateRoute_Success_ShouldCallUpdateOne() {
        // Arrange
        testRoute.setId(testRouteIdUUID);
        testRoute.setEndingPoint("Updated End");
        String routeIdString = testRouteIdUUID.toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq(Filters.eq("id", routeIdString)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        assertDoesNotThrow(() -> routeDao.updateRoute(testRoute));

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        assertEquals(Filters.eq("id", routeIdString), filterCaptor.getValue());
        Document setDoc = updateCaptor.getValue().get("$set", Document.class);
        assertNotNull(setDoc);
        assertEquals(routeIdString, setDoc.getString("id"));
        assertEquals("Updated End", setDoc.getString("endingPoint"));
    }

    @Test
    void updateRoute_NotFound_ShouldThrowDataAccessException() {
        // Arrange
        testRoute.setId(testRouteIdUUID);
        String id = testRoute.getId().toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L);
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            routeDao.updateRoute(testRoute);
        });
        // Проверяем точное сообщение
        assertEquals("Error updating route", exception.getMessage());
        assertEquals("Route not found", exception.getCause().getMessage());

        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    @Test
    void updateRoute_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testRoute.setId(testRouteIdUUID);
        String id = testRoute.getId().toString();

        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            routeDao.updateRoute(testRoute);
        });
        assertTrue(exception.getMessage().contains("Error updating route"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    // --- Тесты для deleteRoute ---

    @Test
    void deleteRoute_Success_ShouldCallDeleteOne() {
        // Arrange
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", testRouteIdStr))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> routeDao.deleteRoute(testRouteIdStr));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", testRouteIdStr)));
    }

    @Test
    void deleteRoute_NotFound_ShouldCallDeleteOneAndNotThrow() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(0L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", nonExistentId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> routeDao.deleteRoute(nonExistentId));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", nonExistentId)));
    }

    @Test
    void deleteRoute_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.deleteOne(eq(Filters.eq("id", id))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            routeDao.deleteRoute(id);
        });
        assertTrue(exception.getMessage().contains("Error deleting route"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", id)));
    }
}