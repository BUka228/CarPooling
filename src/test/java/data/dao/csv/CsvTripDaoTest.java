package data.dao.csv;

import data.model.database.Trip;
import data.model.record.TripRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvTripDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private CsvTripDao tripDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-trips.csv").toFile();
        tripDao = new CsvTripDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateTrip_Success() {
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        String tripId = tripDao.createTrip(tripRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(tripId);
        assertDoesNotThrow(() -> UUID.fromString(tripId));

        // Проверяем, что поездка была добавлена
        Optional<TripRecord> foundTrip = tripDao.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals("planned", foundTrip.get().getStatus());
    }

    @Test
    void testCreateTrip_Failure() {
        // Создаем поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        // Делаем файл недоступным для записи
        tempFile.setReadOnly();

        // Ожидаем исключение при создании поездки
        assertThrows(DataAccessException.class, () -> tripDao.createTrip(tripRecord));
    }




    @Test
    void testGetTripById_Success() {
        // Создаем тестовую поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        String tripId = tripDao.createTrip(tripRecord);

        // Получаем поездку по ID
        Optional<TripRecord> foundTrip = tripDao.getTripById(tripId);

        // Проверяем, что поездка найдена
        assertTrue(foundTrip.isPresent());
        assertEquals(tripId, foundTrip.get().getId());
        assertEquals("planned", foundTrip.get().getStatus());
    }

    @Test
    void testGetTripById_NotFound() {
        // Пытаемся получить несуществующую поездку
        Optional<TripRecord> foundTrip = tripDao.getTripById("non-existent-id");

        // Проверяем, что поездка не найдена
        assertFalse(foundTrip.isPresent());
    }

    @Test
    void testUpdateTrip_Success() {
        // Создаем тестовую поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        String tripId = tripDao.createTrip(tripRecord);

        // Обновляем поездку
        tripRecord.setStatus("completed");
        tripDao.updateTrip(tripRecord);

        // Проверяем, что поездка обновлена
        Optional<TripRecord> updatedTrip = tripDao.getTripById(tripId);
        assertTrue(updatedTrip.isPresent());
        assertEquals("completed", updatedTrip.get().getStatus());
    }

    @Test
    void testUpdateTrip_NotFound() {
        // Пытаемся обновить несуществующую поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setId("non-existent-id");
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        // Ожидаем исключение при обновлении
        assertThrows(DataAccessException.class, () -> tripDao.updateTrip(tripRecord));
    }

    @Test
    void testDeleteTrip_Success() {
        // Создаем тестовую поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        String tripId = tripDao.createTrip(tripRecord);

        // Удаляем поездку
        tripDao.deleteTrip(tripId);

        // Проверяем, что поездка удалена
        Optional<TripRecord> deletedTrip = tripDao.getTripById(tripId);
        assertFalse(deletedTrip.isPresent());
    }

    @Test
    void testDeleteTrip_NotFound() {
        // Пытаемся удалить несуществующую поездку
        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip("non-existent-id"));
    }

    @Test
    void testReadAllTrips_Success() throws IOException {
        // Создаем несколько тестовых поездок
        TripRecord trip1 = new TripRecord();
        trip1.setUserId("user-1");
        trip1.setRouteId("route-1");
        trip1.setDepartureTime(new Date());
        trip1.setMaxPassengers((byte) 4);
        trip1.setCreationDate(new Date());
        trip1.setStatus("planned");
        trip1.setEditable(true);

        TripRecord trip2 = new TripRecord();
        trip2.setUserId("user-2");
        trip2.setRouteId("route-2");
        trip2.setDepartureTime(new Date());
        trip2.setMaxPassengers((byte) 4);
        trip2.setCreationDate(new Date());
        trip2.setStatus("completed");
        trip2.setEditable(false);

        tripDao.createTrip(trip1);
        tripDao.createTrip(trip2);

        // Проверяем, что все поездки записаны и прочитаны
        Optional<TripRecord> foundTrip1 = tripDao.getTripById(trip1.getId());
        Optional<TripRecord> foundTrip2 = tripDao.getTripById(trip2.getId());

        assertTrue(foundTrip1.isPresent());
        assertTrue(foundTrip2.isPresent());
        assertEquals("planned", foundTrip1.get().getStatus());
        assertEquals("completed", foundTrip2.get().getStatus());
    }
}