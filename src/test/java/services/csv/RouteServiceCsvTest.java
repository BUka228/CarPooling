package services.csv;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.csv.CsvRouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.service.RouteServiceException;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.impl.RouteServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RouteServiceCsvTest {

    private RouteService routeService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный CSV-файл
        File csvFile = tempDir.resolve("routes.csv").toFile();

        // Инициализируем DAO и сервис
        RouteDao routeDao = new CsvRouteDao(csvFile.getAbsolutePath());
        routeService = new RouteServiceImpl(routeDao);
    }

    @Test
    void testCreateRouteSuccess() throws RouteServiceException {
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
    void testCreateRouteFailure() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertFalse(routeService.createRoute(route).isEmpty());
    }

    @Test
    void testGetRouteByIdSuccess() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);

        Optional<Route> foundRoute = routeService.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals("Москва", foundRoute.get().getStartPoint());
    }

    @Test
    void testGetRouteByIdFailure() throws RouteServiceException {
        Optional<Route> route = routeService.getRouteById("non-existent-id");
        assertFalse(route.isPresent());
    }

    @Test
    void testUpdateRouteSuccess() throws RouteServiceException {
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
    void testUpdateRouteFailure() {
        Route route = new Route();
        route.setId("non-existent-id");
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertThrows(RouteServiceException.class, () -> routeService.updateRoute(route));
    }

    @Test
    void testDeleteRouteSuccess() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);
        routeService.deleteRoute(routeId);

        Optional<Route> deletedRoute = routeService.getRouteById(routeId);
        assertFalse(deletedRoute.isPresent());
    }

    @Test
    void testDeleteRouteFailure() {
        assertThrows(RouteServiceException.class, () -> routeService.deleteRoute("non-existent-id"));
    }

    @Test
    void testFindRoutesByStartPointSuccess() throws RouteServiceException {
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
    void testFindRoutesByStartPointFailure() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);
        routeService.createRoute(route);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartPoint("Казань"));
    }

    @Test
    void testFindRoutesByEndPointSuccess() throws RouteServiceException {
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

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Москва"));
    }

    @Test
    void testFindRoutesByEndPointFailure() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);
        routeService.createRoute(route);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Казань"));
    }

    @Test
    void testFindRoutesByStartAndEndPointsSuccess() throws RouteServiceException {
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
    void testFindRoutesByStartAndEndPointsFailure() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);
        routeService.createRoute(route);

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartAndEndPoints("Москва", "Казань"));
    }

    @Test
    void testGetRoutesByUser() {
        assertThrows(RouteServiceException.class, () -> routeService.getRoutesByUser("user-id"));
    }

    @Test
    void testGetRoutesByDate() {
        assertThrows(RouteServiceException.class, () -> routeService.getRoutesByDate("2023-12-01"));
    }
}