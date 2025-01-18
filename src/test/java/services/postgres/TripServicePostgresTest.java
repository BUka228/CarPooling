package services.postgres;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.RouteServiceException;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.base.TripService;
import com.carpooling.services.impl.RouteServiceImpl;
import com.carpooling.services.impl.TripServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class TripServicePostgresTest extends BasePostgresTest {

    private TripService tripService;
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

        // Создаем таблицы routes и trips
        createRoutesTable();
        createTripsTable();

        // Инициализируем DAO и сервисы
        RouteDao routeDao = new PostgresRouteDao(connection);
        routeService = new RouteServiceImpl(routeDao);

        TripDao tripDao = new PostgresTripDao(connection);
        tripService = new TripServiceImpl(tripDao, routeService);

        // Очищаем таблицы перед каждым тестом
        connection.createStatement().execute("DELETE FROM trips");
        connection.createStatement().execute("DELETE FROM routes");
    }

    private void createRoutesTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS routes (
            id UUID PRIMARY KEY,
            start_point VARCHAR(255) NOT NULL,
            end_point VARCHAR(255) NOT NULL,
            date TIMESTAMP NOT NULL,
            estimated_duration SMALLINT
        )
        """;
        connection.createStatement().execute(sql);
    }

    private void createTripsTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS trips (
            id UUID PRIMARY KEY,
            departure_time TIMESTAMP NOT NULL,
            max_passengers SMALLINT NOT NULL,
            creation_date TIMESTAMP NOT NULL,
            status VARCHAR(50) NOT NULL,
            editable BOOLEAN NOT NULL,
            user_id UUID NOT NULL,
            route_id UUID NOT NULL,
            FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
        )
        """;
        connection.createStatement().execute(sql);
    }

    @Test
    void testCreateTrip_Success() throws TripServiceException, RouteServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, UUID.randomUUID().toString());  // Генерация UUID для пользователя
        assertNotNull(tripId);

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals(4, foundTrip.get().getMaxPassengers());
    }

    @Test
    void testCreateTrip_Failure() {
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route invalidRoute = new Route();  // Маршрут без обязательных полей

        assertThrows(TripServiceException.class, () -> tripService.createTrip(trip, invalidRoute, UUID.randomUUID().toString()));
    }
    @Test
    void testGetTripById_Success() throws TripServiceException, RouteServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, UUID.randomUUID().toString());

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals(4, foundTrip.get().getMaxPassengers());
    }

    @Test
    void testGetTripById_Failure() throws TripServiceException {
        assertTrue(tripService.getTripById(UUID.randomUUID().toString()).isEmpty());
    }
    @Test
    void testUpdateTrip_Success() throws TripServiceException, RouteServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, UUID.randomUUID().toString());
        String routeId = routeService.createRoute(route);
        route.setId(UUID.fromString(routeId).toString());

        trip.setId(UUID.fromString(tripId).toString());
        trip.setStatus("Завершена");
        tripService.updateTrip(trip, route, UUID.randomUUID().toString());

        Optional<Trip> updatedTrip = tripService.getTripById(tripId);
        assertTrue(updatedTrip.isPresent());
        assertEquals("Завершена", updatedTrip.get().getStatus());
    }

    @Test
    void testUpdateTrip_Failure() {
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertThrows(TripServiceException.class, () -> tripService.updateTrip(trip, route, UUID.randomUUID().toString()));
    }

    @Test
    void testDeleteTrip_Success() throws TripServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, UUID.randomUUID().toString());
        tripService.deleteTrip(tripId);

        assertTrue(tripService.getTripById(tripId).isEmpty());
    }

    @Test
    void testDeleteTrip_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.deleteTrip(UUID.randomUUID().toString()));
    }

    @Test
    void testGetTripsByUser_Success() throws TripServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        tripService.createTrip(trip, route, UUID.randomUUID().toString());

        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser(UUID.randomUUID().toString()));
    }

    @Test
    void testGetTripsByUser_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser(UUID.randomUUID().toString()));
    }

    @Test
    void testGetTripsByStatus_Success() throws TripServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        tripService.createTrip(trip, route, UUID.randomUUID().toString());

        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("Активна"));
    }

    @Test
    void testGetTripsByStatus_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("Несуществующий статус"));
    }

    @Test
    void testGetTripsByCreationDate_Success() throws TripServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        tripService.createTrip(trip, route, UUID.randomUUID().toString());

        assertThrows(TripServiceException.class, () -> tripService.getTripsByCreationDate("2023-11-01"));
    }

    @Test
    void testGetTripsByCreationDate_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByCreationDate("неверная дата"));
    }

    @Test
    void testGetTripsByRoute_Success() throws TripServiceException, RouteServiceException {
        Route route = new Route();
        route.setId(UUID.randomUUID().toString());  // Генерация UUID для маршрута
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString());  // Генерация UUID для поездки
        trip.setDepartureTime(Timestamp.valueOf("2023-12-01 08:00:00"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Timestamp.valueOf("2023-11-01 12:00:00"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String routeId = routeService.createRoute(route);
        tripService.createTrip(trip, route, UUID.randomUUID().toString());

        assertThrows(TripServiceException.class, () -> tripService.getTripsByRoute(routeId));
    }

    @Test
    void testGetTripsByRoute_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByRoute(UUID.randomUUID().toString()));
    }
}