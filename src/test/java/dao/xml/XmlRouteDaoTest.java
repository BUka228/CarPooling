package dao.xml;

import com.carpooling.dao.xml.XmlRouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlRouteDaoTest {

    private XmlRouteDao routeDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() {
        String testFileName = "test-routes.xml";
        tempFile = tempDir.resolve(testFileName).toFile();
        routeDao = new XmlRouteDao(tempFile.getAbsolutePath());
    }

    private Route createTestRoute() {
        Route route = new Route();
        route.setStartingPoint("City A");
        route.setEndingPoint("City B");
        route.setDate(LocalDateTime.now());
        route.setEstimatedDuration((short) 120); // 2 hours
        return route;
    }

    @Test
    void createRoute_Success() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<Route> foundRouteOpt = routeDao.getRouteById(id);
        assertTrue(foundRouteOpt.isPresent(), "Route should be found after creation");
        Route foundRoute = foundRouteOpt.get();

        assertEquals(generatedUUID, foundRoute.getId());
        assertEquals(route.getStartingPoint(), foundRoute.getStartingPoint());
        assertEquals(route.getEndingPoint(), foundRoute.getEndingPoint());
        assertEquals(route.getEstimatedDuration(), foundRoute.getEstimatedDuration());
        assertNotNull(foundRoute.getDate());
    }

    @Test
    void createRoute_DataAccessException_OnFileError() {
        Route route = createTestRoute();
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> routeDao.createRoute(route));
        tempFile.setWritable(true);
    }

    @Test
    void createRoute_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> routeDao.createRoute(null));
    }

    @Test
    void getRouteById_Success() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);
        Optional<Route> foundRoute = routeDao.getRouteById(id);
        assertTrue(foundRoute.isPresent());
        assertEquals(UUID.fromString(id), foundRoute.get().getId());
        assertEquals(route.getStartingPoint(), foundRoute.get().getStartingPoint());
    }

    @Test
    void getRouteById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<Route> foundRoute = routeDao.getRouteById(nonExistentId);
        assertFalse(foundRoute.isPresent());
    }

    @Test
    void getRouteById_DataAccessException_OnFileError() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);
        assertTrue(tempFile.delete());
        assertThrows(IllegalArgumentException.class, () -> routeDao.getRouteById(id));
    }

    @Test
    void updateRoute_Success() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);
        UUID routeUUID = UUID.fromString(id);

        Route createdRoute = routeDao.getRouteById(id).orElseThrow(() -> new AssertionError("Failed to retrieve route for update test"));

        createdRoute.setEndingPoint("City C");
        createdRoute.setEstimatedDuration((short) 150);
        routeDao.updateRoute(createdRoute);

        Optional<Route> updatedRouteOpt = routeDao.getRouteById(id);
        assertTrue(updatedRouteOpt.isPresent());
        Route updatedRoute = updatedRouteOpt.get();

        assertEquals("City A", updatedRoute.getStartingPoint());
        assertEquals("City C", updatedRoute.getEndingPoint());
        assertEquals((short) 150, updatedRoute.getEstimatedDuration());
        assertEquals(routeUUID, updatedRoute.getId());
    }

    @Test
    void updateRoute_NotFound() {
        Route nonExistentRoute = createTestRoute();
        nonExistentRoute.setId(UUID.randomUUID());
        assertThrows(DataAccessException.class, () -> routeDao.updateRoute(nonExistentRoute));
    }

    @Test
    void updateRoute_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> routeDao.updateRoute(null));
    }

    @Test
    void deleteRoute_Success() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);
        assertTrue(routeDao.getRouteById(id).isPresent());
        routeDao.deleteRoute(id);
        assertFalse(routeDao.getRouteById(id).isPresent());
    }

    @Test
    void deleteRoute_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> routeDao.deleteRoute(nonExistentId));
    }

    @Test
    void deleteRoute_DataAccessException_OnFileError() throws DataAccessException {
        Route route = createTestRoute();
        String id = routeDao.createRoute(route);
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> routeDao.deleteRoute(id));
        tempFile.setWritable(true);
    }
}