package dao.routedao;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractRouteDaoTest {

    protected RouteDao routeDao;

    @BeforeEach
    public void setUp() {
        routeDao = createRouteDao();
    }

    @AfterEach
    public void tearDown() {
        cleanUp();
    }

    protected abstract RouteDao createRouteDao();
    protected abstract void cleanUp();

    @Test
    void createRoute_successful() {
        Route route = new Route();
        route.setStartingPoint("City A");
        route.setEndingPoint("City B");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 120);

        String routeId = routeDao.createRoute(route);

        assertNotNull(routeId, "Идентификатор маршрута не должен быть null");
        Optional<Route> createdRoute = routeDao.getRouteById(routeId);
        assertTrue(createdRoute.isPresent(), "Маршрут должен быть создан");
        assertEquals("City A", createdRoute.get().getStartingPoint(), "Начальная точка должна совпадать");
    }

    @Test
    void createRoute_withInvalidData_throwsException() {
        Route route = new Route();
        route.setStartingPoint(null); // Неверная начальная точка
        route.setEndingPoint("City B");      // Неверная конечная точка
        route.setDate(null);          // Неверная дата
        route.setEstimatedDuration((short) -1); // Неверная продолжительность

        assertThrows(DataAccessException.class, () -> routeDao.createRoute(route),
                "Должно выброситься исключение при создании маршрута с некорректными данными");
    }

    @Test
    void getRouteById_successful() {
        Route route = new Route();
        route.setStartingPoint("City A");
        route.setEndingPoint("City B");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 120);
        String routeId = routeDao.createRoute(route);

        Optional<Route> retrievedRoute = routeDao.getRouteById(routeId);

        assertTrue(retrievedRoute.isPresent(), "Маршрут должен быть найден");
        assertEquals("City B", retrievedRoute.get().getEndingPoint(), "Конечная точка должна совпадать");
    }

    @Test
    void getRouteById_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Некорректный формат UUID

        assertThrows(DataAccessException.class, () -> routeDao.getRouteById(invalidId),
                "Должно выброситься исключение при запросе маршрута с некорректным ID");
    }

    @Test
    void getRouteById_withNonExistingId_returnsEmpty() {
        String nonExistingId = UUID.randomUUID().toString(); // Существующий, но не записанный ID

        Optional<Route> retrievedRoute = routeDao.getRouteById(nonExistingId);

        assertFalse(retrievedRoute.isPresent(), "Не должно быть маршрута с несуществующим ID");
    }

    @Test
    void updateRoute_successful() {
        Route route = new Route();
        route.setStartingPoint("City A");
        route.setEndingPoint("City B");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 120);
        String routeId = routeDao.createRoute(route);

        Route updatedRoute = routeDao.getRouteById(routeId).get();
        updatedRoute.setEndingPoint("City C");
        routeDao.updateRoute(updatedRoute);

        Optional<Route> retrievedRoute = routeDao.getRouteById(routeId);
        assertTrue(retrievedRoute.isPresent(), "Маршрут должен существовать после обновления");
        assertEquals("City C", retrievedRoute.get().getEndingPoint(), "Конечная точка должна быть обновлена");
    }

    @Test
    void updateRoute_withNonExistingRoute_throwsException() {
        Route nonExistingRoute = new Route();
        nonExistingRoute.setId(UUID.randomUUID());
        nonExistingRoute.setStartingPoint("City X");
        nonExistingRoute.setEndingPoint("City Y");

        assertThrows(DataAccessException.class, () -> routeDao.updateRoute(nonExistingRoute),
                "Должно выброситься исключение при обновлении несуществующего маршрута");
    }

    // Тесты для deleteRoute
    @Test
    void deleteRoute_successful() {
        Route route = new Route();
        route.setStartingPoint("City A");
        route.setEndingPoint("City B");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 120);
        String routeId = routeDao.createRoute(route);

        routeDao.deleteRoute(routeId);

        Optional<Route> retrievedRoute = routeDao.getRouteById(routeId);
        assertFalse(retrievedRoute.isPresent(), "Маршрут должен быть удален");
    }

    @Test
    void deleteRoute_withNonExistingId_doesNotThrowException() {
        String nonExistingId = UUID.randomUUID().toString();

        assertDoesNotThrow(() -> routeDao.deleteRoute(nonExistingId),
                "Удаление несуществующего маршрута не должно выбрасывать исключение");
    }

    @Test
    void deleteRoute_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Некорректный формат UUID

        assertThrows(DataAccessException.class, () -> routeDao.deleteRoute(invalidId),
                "Должно выброситься исключение при удалении маршрута с некорректным ID");
    }
}