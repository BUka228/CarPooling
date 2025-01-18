package services.csv;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.csv.CsvRouteDao;
import com.carpooling.dao.csv.CsvTripDao;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.base.TripService;
import com.carpooling.services.impl.RouteServiceImpl;
import com.carpooling.services.impl.TripServiceImpl;
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

class TripServiceCsvTest {

    private TripService tripService;


    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временные CSV-файлы для поездок и маршрутов
        File tripCsvFile = tempDir.resolve("trips.csv").toFile();
        File routeCsvFile = tempDir.resolve("routes.csv").toFile();

        // Инициализируем DAO и сервисы
        TripDao tripDao = new CsvTripDao(tripCsvFile.getAbsolutePath());
        RouteDao routeDao = new CsvRouteDao(routeCsvFile.getAbsolutePath());
        RouteService routeService = new RouteServiceImpl(routeDao);
        tripService = new TripServiceImpl(tripDao, routeService);
    }

    @Test
    void testCreateTripSuccess() throws TripServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, "user-id");
        assertNotNull(tripId);

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals("Активна", foundTrip.get().getStatus());
    }

    @Test
    void testCreateTripFailure() throws TripServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        assertFalse(tripService.createTrip(trip, route, "user-id").isEmpty());
    }

    @Test
    void testGetTripByIdSuccess() throws TripServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, "user-id");

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals("Активна", foundTrip.get().getStatus());
    }

    @Test
    void testGetTripByIdFailure() throws TripServiceException {
        Optional<Trip> trip = tripService.getTripById("non-existent-id");
        assertFalse(trip.isPresent());
    }

    @Test
    void testUpdateTripSuccess() throws TripServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);



        String tripId = tripService.createTrip(trip, route, "user-id");

        trip.setId(tripId);
        trip.setStatus("Завершена");
        tripService.updateTrip(trip, route, "user-id");

        Optional<Trip> updatedTrip = tripService.getTripById(tripId);
        assertTrue(updatedTrip.isPresent());
        assertEquals("Завершена", updatedTrip.get().getStatus());
    }

    @Test
    void testUpdateTripFailure() {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setId("non-existent-id");
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        assertThrows(TripServiceException.class, () -> tripService.updateTrip(trip, route, "user-id"));
    }

    @Test
    void testDeleteTripSuccess() throws TripServiceException {
        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2023-11-01"));
        trip.setStatus("Активна");
        trip.setEditable(true);

        String tripId = tripService.createTrip(trip, route, "user-id");
        tripService.deleteTrip(tripId);

        Optional<Trip> deletedTrip = tripService.getTripById(tripId);
        assertFalse(deletedTrip.isPresent());
    }

    @Test
    void testDeleteTripFailure() {
        assertThrows(TripServiceException.class, () -> tripService.deleteTrip("non-existent-id"));
    }

    @Test
    void testGetTripsByUser() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser("user-id"));
    }

    @Test
    void testGetTripsByStatus() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("Активна"));
    }

    @Test
    void testGetTripsByCreationDate() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByCreationDate("2023-11-01"));
    }

    @Test
    void testGetTripsByRoute() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByRoute("route-id"));
    }
}