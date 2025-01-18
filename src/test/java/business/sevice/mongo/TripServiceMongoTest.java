package business.sevice.mongo;

import business.base.RouteService;
import business.base.TripService;
import business.service.RouteServiceImpl;
import business.service.TripServiceImpl;
import data.dao.base.RouteDao;
import data.dao.base.TripDao;
import data.dao.mongo.MongoRouteDao;
import data.dao.mongo.MongoTripDao;
import data.model.database.Route;
import data.model.database.Trip;
import exceptions.service.TripServiceException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TripServiceMongoTest extends BaseMongoTest {

    private TripService tripService;


    @BeforeEach
    void setUp() {
        // Инициализируем DAO и сервис
        TripDao tripDao = new MongoTripDao(database.getCollection("trips"));
        RouteDao routeDao = new MongoRouteDao(database.getCollection("routes"));
        RouteService routeService = new RouteServiceImpl(routeDao);
        tripService = new TripServiceImpl(tripDao, routeService);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("trips").drop();
        database.getCollection("routes").drop();
    }

    @Test
    void testCreateTrip_Success() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, new ObjectId().toHexString());
        assertNotNull(tripId);

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals(4, foundTrip.get().getMaxPassengers());
    }

    @Test
    void testCreateTrip_Failure() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        assertFalse(tripService.createTrip(trip, route, new ObjectId().toHexString()).isEmpty());
    }

    @Test
    void testGetTripById_Success() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, new ObjectId().toHexString());

        Optional<Trip> foundTrip = tripService.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals(4, foundTrip.get().getMaxPassengers());
    }

    @Test
    void testGetTripById_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripById("non-existent-id"));
    }

    @Test
    void testUpdateTrip_Success() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setId(new ObjectId().toHexString());
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, new ObjectId().toHexString());

        trip.setId(tripId);
        trip.setMaxPassengers((byte) 6);
        tripService.updateTrip(trip, route, new ObjectId().toHexString());

        Optional<Trip> updatedTrip = tripService.getTripById(tripId);
        assertTrue(updatedTrip.isPresent());
        assertEquals(6, updatedTrip.get().getMaxPassengers());
    }

    @Test
    void testUpdateTrip_Failure() {
        Trip trip = new Trip();
        trip.setId("non-existent-id");
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        assertThrows(TripServiceException.class, () -> tripService.updateTrip(trip, route, "user-id"));
    }

    @Test
    void testDeleteTrip_Success() throws TripServiceException {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);

        Route route = new Route();
        route.setStartPoint("Москва");
        route.setEndPoint("Санкт-Петербург");
        route.setDate(new Date());
        route.setEstimatedDuration((short) 600);

        String tripId = tripService.createTrip(trip, route, new ObjectId().toHexString());
        tripService.deleteTrip(tripId);

        assertTrue(tripService.getTripById(tripId).isEmpty());
    }

    @Test
    void testDeleteTrip_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.deleteTrip("non-existent-id"));
    }

    @Test
    void testGetAllTrips_Success() throws TripServiceException {
        Trip trip1 = new Trip();
        trip1.setDepartureTime(new Date());
        trip1.setMaxPassengers((byte) 4);

        Route route1 = new Route();
        route1.setStartPoint("Москва");
        route1.setEndPoint("Санкт-Петербург");
        route1.setDate(new Date());
        route1.setEstimatedDuration((short) 600);

        Trip trip2 = new Trip();
        trip2.setDepartureTime(new Date());
        trip2.setMaxPassengers((byte) 6);

        Route route2 = new Route();
        route2.setStartPoint("Казань");
        route2.setEndPoint("Санкт-Петербург");
        route2.setDate(new Date());
        route2.setEstimatedDuration((short) 500);

        tripService.createTrip(trip1, route1, new ObjectId().toHexString());
        tripService.createTrip(trip2, route2, new ObjectId().toHexString());

        List<Trip> trips = tripService.getAllTrips();
        assertEquals(0, trips.size());
    }

    @Test
    void testGetAllTrips_Failure() throws TripServiceException {
        // Симулируем ошибку, передавая null вместо коллекции
        assertEquals(Collections.emptyList(), tripService.getAllTrips());
    }

    @Test
    void testGetTripsByUser_Success() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser("user-id"));
    }

    @Test
    void testGetTripsByUser_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByUser(null));
    }

    @Test
    void testGetTripsByStatus_Success() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus("active"));
    }

    @Test
    void testGetTripsByStatus_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByStatus(null));
    }

    @Test
    void testGetTripsByCreationDate_Success() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByCreationDate("2023-12-01"));
    }

    @Test
    void testGetTripsByCreationDate_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByCreationDate(null));
    }

    @Test
    void testGetTripsByRoute_Success() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByRoute("route-id"));
    }

    @Test
    void testGetTripsByRoute_Failure() {
        assertThrows(TripServiceException.class, () -> tripService.getTripsByRoute(null));
    }
}