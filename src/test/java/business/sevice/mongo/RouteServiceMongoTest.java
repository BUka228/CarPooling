package business.sevice.mongo;

import business.base.RouteService;
import business.service.RouteServiceImpl;
import data.dao.base.RouteDao;
import data.dao.mongo.MongoRouteDao;
import data.model.database.Route;
import exceptions.service.RouteServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceMongoTest extends BaseMongoTest {

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        // Инициализируем DAO и сервис
        RouteDao routeDao = new MongoRouteDao(database.getCollection("routes"));
        routeService = new RouteServiceImpl(routeDao);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("routes").drop();
    }

    @Test
    void testCreateRoute_Success() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);
        assertNotNull(routeId);

        Optional<Route> foundRoute = routeService.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals("Москва", foundRoute.get().getStartPoint());
    }

    @Test
    void testCreateRoute_Failure_InvalidData() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint(null);
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);
        assertFalse(routeService.createRoute(route).isEmpty());
    }

    @Test
    void testGetRouteById_Success() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);

        Optional<Route> foundRoute = routeService.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals("Санкт-Петербург", foundRoute.get().getEndPoint());
    }

    @Test
    void testGetRouteById_Failure_NotFound() throws RouteServiceException {
        assertThrows(RouteServiceException.class, () -> routeService.getRouteById("non-existent-id"));
    }

    @Test
    void testUpdateRoute_Success() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);

        route.setId(routeId);
        route.setStartPoint("Казань");
        routeService.updateRoute(route);

        Optional<Route> updatedRoute = routeService.getRouteById(routeId);
        assertTrue(updatedRoute.isPresent());
        assertEquals("Казань", updatedRoute.get().getStartPoint());
    }

    @Test
    void testUpdateRoute_Failure_InvalidRouteId() {
        Route route = new Route();
        route.setId("invalid-route-id");
        route.setStartPoint("Казань");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertThrows(RouteServiceException.class, () ->
                routeService.updateRoute(route)
        );
    }

    @Test
    void testDeleteRoute_Success() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);
        routeService.deleteRoute(routeId);

        assertTrue(routeService.getRouteById(routeId).isEmpty());
    }

    @Test
    void testDeleteRoute_Failure_InvalidRouteId() {
        assertThrows(RouteServiceException.class, () ->
                routeService.deleteRoute("invalid-route-id")
        );
    }

    @Test
    void testFindRoutesByStartPoint_Success() throws RouteServiceException {
        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(Date.valueOf("2023-12-01"));
        route1.setEstimatedDuration((short) 600);
        routeService.createRoute(route1);

        Route route2 = new Route();
        route2.setStartPoint("Москва");
        route2.setEndPoint("Казань");
        route2.setDate(Date.valueOf("2023-12-02"));
        route2.setEstimatedDuration((short) 400);
        routeService.createRoute(route2);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartPoint("Москва"));
    }

    @Test
    void testFindRoutesByStartPoint_Failure_NotFound() throws RouteServiceException {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartPoint("Новосибирск"));
    }

    @Test
    void testFindRoutesByEndPoint_Success() throws RouteServiceException {
        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(Date.valueOf("2023-12-01"));
        route1.setEstimatedDuration((short) 600);
        routeService.createRoute(route1);

        Route route2 = new Route();
        route2.setStartPoint("Казань");
        route2.setEndPoint("Санкт-Петербург");
        route2.setDate(Date.valueOf("2023-12-02"));
        route2.setEstimatedDuration((short) 500);
        routeService.createRoute(route2);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Санкт-Петербург"));
    }

    @Test
    void testFindRoutesByEndPoint_Failure_NotFound() throws RouteServiceException {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Новосибирск"));
    }

    @Test
    void testFindRoutesByStartAndEndPoints_Success() throws RouteServiceException {
        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(Date.valueOf("2023-12-01"));
        route1.setEstimatedDuration((short) 600);
        routeService.createRoute(route1);

        Route route2 = new Route();
        route2.setStartPoint("Москва");
        route2.setEndPoint("Казань");
        route2.setDate(Date.valueOf("2023-12-02"));
        route2.setEstimatedDuration((short) 400);
        routeService.createRoute(route2);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartAndEndPoints("Москва", "Санкт-Петербург"));
    }

    @Test
    void testFindRoutesByStartAndEndPoints_Failure_NotFound() throws RouteServiceException {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartAndEndPoints("Москва", "Новосибирск"));
    }

    @Test
    void testGetRoutesByUser_Success() {
        assertThrows(RouteServiceException.class, () ->
                routeService.getRoutesByUser("user-id")
        );
    }

    @Test
    void testGetRoutesByUser_Failure_InvalidUserId() {
        assertThrows(RouteServiceException.class, () ->
                routeService.getRoutesByUser("invalid-user-id")
        );
    }

    @Test
    void testGetRoutesByDate_Success() {
        assertThrows(RouteServiceException.class, () ->
                routeService.getRoutesByDate("2023-12-01")
        );
    }

    @Test
    void testGetRoutesByDate_Failure_InvalidDate() {
        assertThrows(RouteServiceException.class, () ->
                routeService.getRoutesByDate("invalid-date")
        );
    }
}