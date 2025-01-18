package dao.xml;

import com.carpooling.dao.xml.XmlRatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlRatingDaoTest {

    private XmlRatingDao ratingDao;
    @TempDir
    Path tempDir; // Временная директория для тестов

    private java.io.File tempFile;



    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-ratings.xml").toFile();
        ratingDao = new XmlRatingDao(tempFile.getAbsolutePath());
    }


    @Test
    void testCreateRating_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

        // Создаем рейтинг
        String ratingId = ratingDao.createRating(ratingRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(ratingId);
        assertDoesNotThrow(() -> UUID.fromString(ratingId));

        // Проверяем, что рейтинг был добавлен
        Optional<RatingRecord> foundRating = ratingDao.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals(5, foundRating.get().getRating());
    }

    @Test
    void testCreateRating_Failure() {
        // Создаем тестовый рейтинг с некорректными данными (например, null)
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setTripId(null); // Некорректные данные
        tempFile.setReadOnly();


        // Проверяем, что создание рейтинга выбрасывает исключение
        assertThrows(DataAccessException.class, () -> ratingDao.createRating(ratingRecord));
    }

    @Test
    void testGetRatingById_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

        // Создаем рейтинг и получаем его ID
        String ratingId = ratingDao.createRating(ratingRecord);

        // Получаем рейтинг по ID
        Optional<RatingRecord> foundRating = ratingDao.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals(ratingId, foundRating.get().getId());
    }

    @Test
    void testGetRatingById_NotFound() {
        // Пытаемся получить несуществующий рейтинг
        Optional<RatingRecord> foundRating = ratingDao.getRatingById("non-existent-id");
        assertFalse(foundRating.isPresent());
    }

    @Test
    void testUpdateRating_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

        // Создаем рейтинг и получаем его ID
        String ratingId = ratingDao.createRating(ratingRecord);

        // Обновляем рейтинг
        ratingRecord.setComment("Updated comment");
        ratingDao.updateRating(ratingRecord);

        // Проверяем, что рейтинг был обновлен
        Optional<RatingRecord> updatedRating = ratingDao.getRatingById(ratingId);
        assertTrue(updatedRating.isPresent());
        assertEquals("Updated comment", updatedRating.get().getComment());
    }

    @Test
    void testUpdateRating_NotFound() {
        // Пытаемся обновить несуществующий рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setId("non-existent-id");
        ratingRecord.setRating(5);

        // Проверяем, что обновление выбрасывает исключение
        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(ratingRecord));
    }

    @Test
    void testDeleteRating_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Great trip!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

        // Создаем рейтинг и получаем его ID
        String ratingId = ratingDao.createRating(ratingRecord);

        // Удаляем рейтинг
        ratingDao.deleteRating(ratingId);

        // Проверяем, что рейтинг был удален
        Optional<RatingRecord> deletedRating = ratingDao.getRatingById(ratingId);
        assertFalse(deletedRating.isPresent());
    }

    @Test
    void testDeleteRating_NotFound() {
        // Пытаемся удалить несуществующий рейтинг
        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating("non-existent-id"));
    }
}