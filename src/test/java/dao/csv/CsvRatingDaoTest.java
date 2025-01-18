package dao.csv;

import com.carpooling.dao.csv.CsvRatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
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

class CsvRatingDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private CsvRatingDao ratingDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-ratings.csv").toFile();
        ratingDao = new CsvRatingDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateRating_Success() {
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Отличная поездка!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

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
    void testCreateRating_Fail() {
        // Создаем временный файл и делаем его недоступным для записи
        File file = tempDir.resolve("test-ratings-fail.csv").toFile();
        try {
            file.createNewFile();
            file.setReadOnly(); // Делаем файл доступным только для чтения
        } catch (IOException e) {
            fail("Не удалось создать или изменить права доступа к файлу.");
        }

        CsvRatingDao failDao = new CsvRatingDao(file.getAbsolutePath());

        // Пытаемся создать рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(5);
        ratingRecord.setComment("Отличная поездка!");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-1");

        assertThrows(DataAccessException.class, () -> failDao.createRating(ratingRecord));

        // Восстанавливаем права доступа к файлу для последующих тестов
        file.setWritable(true);
    }


    @Test
    void testGetRatingById_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(4);
        ratingRecord.setComment("Хорошая поездка");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-2");

        String ratingId = ratingDao.createRating(ratingRecord);

        // Получаем рейтинг по ID
        Optional<RatingRecord> foundRating = ratingDao.getRatingById(ratingId);

        // Проверяем, что рейтинг найден
        assertTrue(foundRating.isPresent());
        assertEquals(ratingId, foundRating.get().getId());
        assertEquals(4, foundRating.get().getRating());
    }

    @Test
    void testGetRatingById_NotFound() {
        // Пытаемся получить несуществующий рейтинг
        Optional<RatingRecord> foundRating = ratingDao.getRatingById("non-existent-id");

        // Проверяем, что рейтинг не найден
        assertFalse(foundRating.isPresent());
    }

    @Test
    void testUpdateRating_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(3);
        ratingRecord.setComment("Нормальная поездка");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-3");

        String ratingId = ratingDao.createRating(ratingRecord);

        // Обновляем рейтинг
        ratingRecord.setRating(5);
        ratingDao.updateRating(ratingRecord);

        // Проверяем, что рейтинг обновлен
        Optional<RatingRecord> updatedRating = ratingDao.getRatingById(ratingId);
        assertTrue(updatedRating.isPresent());
        assertEquals(5, updatedRating.get().getRating());
    }

    @Test
    void testUpdateRating_NotFound() {
        // Пытаемся обновить несуществующий рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setId("non-existent-id");
        ratingRecord.setRating(1);

        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(ratingRecord));
    }

    @Test
    void testDeleteRating_Success() {
        // Создаем тестовый рейтинг
        RatingRecord ratingRecord = new RatingRecord();
        ratingRecord.setRating(2);
        ratingRecord.setComment("Плохая поездка");
        ratingRecord.setDate(new Date());
        ratingRecord.setTripId("trip-4");

        String ratingId = ratingDao.createRating(ratingRecord);

        // Удаляем рейтинг
        ratingDao.deleteRating(ratingId);

        // Проверяем, что рейтинг удален
        Optional<RatingRecord> deletedRating = ratingDao.getRatingById(ratingId);
        assertFalse(deletedRating.isPresent());
    }

    @Test
    void testDeleteRating_NotFound() {
        // Пытаемся удалить несуществующий рейтинг
        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating("non-existent-id"));
    }
}