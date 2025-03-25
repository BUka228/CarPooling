package dao.triipdao;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTripDaoTest {

    protected TripDao tripDao;

    @BeforeEach
    public void setUp() {
        tripDao = createTripDao();
    }

    @AfterEach
    public void tearDown() {
        cleanUp();
    }

    protected abstract TripDao createTripDao();
    protected abstract void cleanUp();

    // Тесты для createTrip
    @Test
    void createTrip_successful() {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("PLANNED");

        String tripId = tripDao.createTrip(trip);

        assertNotNull(tripId, "Идентификатор поездки не должен быть null");
        Optional<Trip> createdTrip = tripDao.getTripById(tripId);
        assertTrue(createdTrip.isPresent(), "Поездка должна быть создана");
        assertEquals("PLANNED", createdTrip.get().getStatus(), "Статус поездки должен совпадать");
    }

    @Test
    void createTrip_withInvalidData_throwsException() {
        Trip trip = new Trip();
        trip.setDepartureTime(null); // Неверное время отправления
        trip.setMaxPassengers((byte) -1);   // Неверное количество пассажиров
        trip.setStatus(null);        // Неверный статус

        assertThrows(DataAccessException.class, () -> tripDao.createTrip(trip),
                "Должно выброситься исключение при создании поездки с некорректными данными");
    }

    // Тесты для getTripById
    @Test
    void getTripById_successful() {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("PLANNED");
        String tripId = tripDao.createTrip(trip);

        Optional<Trip> retrievedTrip = tripDao.getTripById(tripId);

        assertTrue(retrievedTrip.isPresent(), "Поездка должна быть найдена");
        assertEquals("PLANNED", retrievedTrip.get().getStatus(), "Статус поездки должен совпадать");
    }

    @Test
    void getTripById_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Некорректный формат UUID

        assertThrows(DataAccessException.class, () -> tripDao.getTripById(invalidId),
                "Должно выброситься исключение при запросе поездки с некорректным ID");
    }

    @Test
    void getTripById_withNonExistingId_returnsEmpty() {
        String nonExistingId = UUID.randomUUID().toString(); // Существующий, но не записанный ID

        Optional<Trip> retrievedTrip = tripDao.getTripById(nonExistingId);

        assertFalse(retrievedTrip.isPresent(), "Не должно быть поездки с несуществующим ID");
    }

    // Тесты для updateTrip
    @Test
    void updateTrip_successful() {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("PLANNED");
        String tripId = tripDao.createTrip(trip);

        Trip updatedTrip = tripDao.getTripById(tripId).get();
        updatedTrip.setStatus("CANCELLED");
        tripDao.updateTrip(updatedTrip);

        Optional<Trip> retrievedTrip = tripDao.getTripById(tripId);
        assertTrue(retrievedTrip.isPresent(), "Поездка должна существовать после обновления");
        assertEquals("CANCELLED", retrievedTrip.get().getStatus(), "Статус поездки должен быть обновлен");
    }

    @Test
    void updateTrip_withNonExistingTrip_throwsException() {
        Trip nonExistingTrip = new Trip();
        nonExistingTrip.setId(UUID.randomUUID());
        nonExistingTrip.setStatus("CANCELLED");

        assertThrows(DataAccessException.class, () -> tripDao.updateTrip(nonExistingTrip),
                "Должно выброситься исключение при обновлении несуществующей поездки");
    }

    // Тесты для deleteTrip
    @Test
    void deleteTrip_successful() {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date());
        trip.setMaxPassengers((byte) 4);
        trip.setStatus("PLANNED");
        String tripId = tripDao.createTrip(trip);

        tripDao.deleteTrip(tripId);

        Optional<Trip> retrievedTrip = tripDao.getTripById(tripId);
        assertFalse(retrievedTrip.isPresent(), "Поездка должна быть удалена");
    }

    @Test
    void deleteTrip_withNonExistingId_doesNotThrowException() {
        String nonExistingId = UUID.randomUUID().toString();

        assertDoesNotThrow(() -> tripDao.deleteTrip(nonExistingId),
                "Удаление несуществующей поездки не должно выбрасывать исключение");
    }

    @Test
    void deleteTrip_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Некорректный формат UUID

        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip(invalidId),
                "Должно выброситься исключение при удалении поездки с некорректным ID");
    }
}