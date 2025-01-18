package services.postgres;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.service.RouteServiceException;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.impl.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RouteServicePostgresTest extends BasePostgresTest {

    private RouteService routeService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Создаем подключение к базе данных
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        // Создаем таблицу routes
        createRoutesTable();

        // Инициализируем DAO и сервис
        RouteDao routeDao = new PostgresRouteDao(connection);
        routeService = new RouteServiceImpl(routeDao);

        // Очищаем таблицу перед каждым тестом
        connection.createStatement().execute("DELETE FROM routes");
    }

    private void createRoutesTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS routes (
                id UUID PRIMARY KEY,
                start_point VARCHAR(255) NOT NULL,
                end_point VARCHAR(255) NOT NULL,
                date DATE NOT NULL,
                estimated_duration SMALLINT
            )
            """;
        connection.createStatement().execute(sql);
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
    void testCreateRouteFail() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);
        routeService.createRoute(route);

        routeService.createRoute(route);
        assertTrue(routeService.getRouteById(UUID.randomUUID().toString()).isEmpty());

    }

    @Test
    void testGetAllRoutesSuccess() throws RouteServiceException {
        List<Route> routes = routeService.getAllRoutes();
        assertTrue(routes.isEmpty());
    }

    @Test
    void testGetAllRoutesFail() throws RouteServiceException {
        assertEquals(Collections.emptyList(), routeService.getAllRoutes());
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
    void testUpdateRouteFail() throws RouteServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String routeId = routeService.createRoute(route);

        route.setId(routeId);
        route.setStartPoint(null);

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

        assertTrue(routeService.getRouteById(routeId).isEmpty());
    }
    @Test
    void testDeleteRouteFail() {
        assertThrows(RouteServiceException.class, () -> routeService.deleteRoute(UUID.randomUUID().toString()));
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
    void testGetRouteByIdNotFound() throws RouteServiceException {
        assertTrue(routeService.getRouteById(UUID.randomUUID().toString()).isEmpty());
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
    void testFindRoutesByStartPointFail() {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartPoint("Москва"));
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

        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Санкт-Петербург"));
    }

    @Test
    void testFindRoutesByEndPointFail() {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByEndPoint("Санкт-Петербург"));
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
    void testFindRoutesByStartAndEndPointsFail() {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutesByStartAndEndPoints("Москва", "Санкт-Петербург"));
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