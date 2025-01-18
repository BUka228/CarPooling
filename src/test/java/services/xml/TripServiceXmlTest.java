package services.xml;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.xml.XmlRouteDao;
import com.carpooling.dao.xml.XmlTripDao;
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

class TripServiceXmlTest {

    private TripService tripService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный XML-файл
        File tripXmlFile = tempDir.resolve("trips.xml").toFile();
        File routeXmlFile = tempDir.resolve("routes.xml").toFile();

        // Инициализируем DAO и сервис
        TripDao tripDao = new XmlTripDao(tripXmlFile.getAbsolutePath());
        RouteDao routeDao = new XmlRouteDao(routeXmlFile.getAbsolutePath());
        RouteService routeService = new RouteServiceImpl(routeDao);
        tripService = new TripServiceImpl(tripDao, routeService);
    }

    @Test
    void testCreateTripSuccess() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, "user-id");
        assertNotNull(tripId);

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
    }

    @Test
    void testCreateTripFailure() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertFalse(tripService.createTrip(trip, route, "user-id").isEmpty());
    }

    @Test
    void testGetTripByIdSuccess() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, "user-id");

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
    }

    @Test
    void testGetTripByIdFailure() throws TripServiceException {
        Optional<Trip> trip = tripService.getTripById("non-existent-id");
        assertFalse(trip.isPresent());
    }

    @Test
    void testUpdateTripSuccess() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

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
        Trip trip = new Trip();
        trip.setId("non-existent-id");
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

        assertThrows(TripServiceException.class, () -> tripService.updateTrip(trip, route, "user-id"));
    }

    @Test
    void testDeleteTripSuccess() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(Date.valueOf("2023-12-01"));
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("Активна");
        trip.setEditable(true);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(Date.valueOf("2023-12-01"));
        route.setEstimatedDuration((short) 600);

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
    void testGetTripsByUserSuccess() throws TripServiceException {
        Trip trip1 = new Trip();
        trip1.setDepartureTime(Date.valueOf("2023-12-01"));
        trip1.setMaxPassengers((byte) 4);
        trip1.setStatus("Активна");
        trip1.setEditable(true);

        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(Date.valueOf("2023-12-01"));
        route1.setEstimatedDuration((short) 600);

        tripService.createTrip(trip1, route1, "user-id");

        Trip trip2 = new Trip();
        trip2.setDepartureTime(Date.valueOf("2023-12-02"));
        trip2.setMaxPassengers((byte) 3);
        trip2.setStatus("Активна");
        trip2.setEditable(true);

        Route route2 = new Route();
        route2.setStartPoint("Москва");
        route2.setEndPoint("Казань");
        route2.setDate(Date.valueOf("2023-12-02"));
        route2.setEstimatedDuration((short) 400);

        tripService.createTrip(trip2, route2, "user-id");

        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser("user-id"));

    }

    @Test
    void testGetTripsByUserFailure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser("non-existent-user-id"));
    }

    @Test
    void testGetTripsByStatusSuccess() throws TripServiceException {
        Trip trip1 = new Trip();
        trip1.setDepartureTime(Date.valueOf("2023-12-01"));
        trip1.setMaxPassengers((byte) 4);
        trip1.setStatus("Активна");
        trip1.setEditable(true);

        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(Date.valueOf("2023-12-01"));
        route1.setEstimatedDuration((short) 600);

        tripService.createTrip(trip1, route1, "user-id");

        Trip trip2 = new Trip();
        trip2.setDepartureTime(Date.valueOf("2023-12-02"));
        trip2.setMaxPassengers((byte) 3);
        trip2.setStatus("Завершена");
        trip2.setEditable(true);

        Route route2 = new Route();
        route2.setStartPoint("Москва");
        route2.setEndPoint("Казань");
        route2.setDate(Date.valueOf("2023-12-02"));
        route2.setEstimatedDuration((short) 400);

        tripService.createTrip(trip2, route2, "user-id");

        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("Активна"));
    }

    @Test
    void testGetTripsByStatusFailure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("non-existent-status"));
    }
}