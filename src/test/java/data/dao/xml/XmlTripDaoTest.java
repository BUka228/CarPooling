package data.dao.xml;

import data.dao.base.TripDao;
import data.model.database.Trip;
import data.model.record.TripRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlTripDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private TripDao tripDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-users.csv").toFile();
        tripDao = new XmlTripDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateTrip_Success() {
        // Создаем тестовую поездку
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId("user-1");
        tripRecord.setRouteId("route-1");
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("planned");
        tripRecord.setEditable(true);

        // Создаем поездку
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
        // Создаем тестовую поездку с некорректными данными (например, null)
        TripRecord tripRecord = new TripRecord();
        tripRecord.setUserId(null); // Некорректные данные

        tempFile.setReadOnly();

        // Проверяем, что создание поездки выбрасывает исключение
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

        // Создаем поездку и получаем её ID
        String tripId = tripDao.createTrip(tripRecord);

        // Получаем поездку по ID
        Optional<TripRecord> foundTrip = tripDao.getTripById(tripId);
        assertTrue(foundTrip.isPresent());
        assertEquals(tripId, foundTrip.get().getId());
    }

    @Test
    void testGetTripById_NotFound() {
        // Пытаемся получить несуществующую поездку
        Optional<TripRecord> foundTrip = tripDao.getTripById("non-existent-id");
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

        // Создаем поездку и получаем её ID
        String tripId = tripDao.createTrip(tripRecord);

        // Обновляем поездку
        tripRecord.setStatus("completed");
        tripDao.updateTrip(tripRecord);

        // Проверяем, что поездка была обновлена
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

        // Проверяем, что обновление выбрасывает исключение
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

        // Создаем поездку и получаем её ID
        String tripId = tripDao.createTrip(tripRecord);

        // Удаляем поездку
        tripDao.deleteTrip(tripId);

        // Проверяем, что поездка была удалена
        Optional<TripRecord> deletedTrip = tripDao.getTripById(tripId);
        assertFalse(deletedTrip.isPresent());
    }

    @Test
    void testDeleteTrip_NotFound() {
        // Пытаемся удалить несуществующую поездку
        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip("non-existent-id"));
    }
}