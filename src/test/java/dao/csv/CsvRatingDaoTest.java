package dao.csv;


import com.carpooling.dao.csv.CsvRatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvRatingDaoTest {

    private CsvRatingDao ratingDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        String testFileName = "test-ratings.csv";
        Path filePath = tempDir.resolve(testFileName);
        Files.createFile(filePath);
        tempFile = filePath.toFile();
        ratingDao = new CsvRatingDao(tempFile.getAbsolutePath());
    }

    private Rating createTestRating() {
        Rating rating = new Rating();
        // Устанавливаем только поля, аннотированные @CsvBindByName
        rating.setRating(5);
        rating.setComment("Excellent trip!");
        rating.setDate(new Date());
        // rating.setTrip(new Trip()); // Поле Trip не аннотировано @CsvBindByName
        return rating;
    }

    @Test
    void createRating_Success() throws DataAccessException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<Rating> foundRatingOpt = ratingDao.getRatingById(id);
        assertTrue(foundRatingOpt.isPresent());
        Rating foundRating = foundRatingOpt.get();

        assertEquals(generatedUUID, foundRating.getId());
        assertEquals(rating.getRating(), foundRating.getRating());
        assertEquals(rating.getComment(), foundRating.getComment());
        assertNotNull(foundRating.getDate());
    }

    @Test
    void createRating_DataAccessException_OnFileError() {
        Rating rating = createTestRating();
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> ratingDao.createRating(rating));
        tempFile.setWritable(true);
    }

    @Test
    void createRating_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> ratingDao.createRating(null));
    }

    @Test
    void getRatingById_Success() throws DataAccessException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);
        Optional<Rating> foundRating = ratingDao.getRatingById(id);
        assertTrue(foundRating.isPresent());
        assertEquals(UUID.fromString(id), foundRating.get().getId());
        assertEquals(rating.getRating(), foundRating.get().getRating());
    }

    @Test
    void getRatingById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<Rating> foundRating = ratingDao.getRatingById(nonExistentId);
        assertFalse(foundRating.isPresent());
    }

    @Test
    void getRatingById_DataAccessException_OnFileError() throws DataAccessException, IOException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);
        assertTrue(Files.deleteIfExists(tempFile.toPath()));
        assertThrows(DataAccessException.class, () -> ratingDao.getRatingById(id));
    }

    @Test
    void updateRating_Success() throws DataAccessException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);
        UUID ratingUUID = UUID.fromString(id);

        Rating createdRating = ratingDao.getRatingById(id).orElseThrow(() -> new AssertionError("Failed to retrieve rating for update test"));

        createdRating.setRating(1);
        createdRating.setComment("Very bad trip.");
        ratingDao.updateRating(createdRating);

        Optional<Rating> updatedRatingOpt = ratingDao.getRatingById(id);
        assertTrue(updatedRatingOpt.isPresent());
        Rating updatedRating = updatedRatingOpt.get();

        assertEquals(1, updatedRating.getRating());
        assertEquals("Very bad trip.", updatedRating.getComment());
        assertEquals(ratingUUID, updatedRating.getId());
    }

    @Test
    void updateRating_NotFound() {
        Rating nonExistentRating = createTestRating();
        nonExistentRating.setId(UUID.randomUUID());
        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(nonExistentRating));
    }

    @Test
    void updateRating_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> ratingDao.updateRating(null));
    }

    @Test
    void deleteRating_Success() throws DataAccessException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);
        assertTrue(ratingDao.getRatingById(id).isPresent());
        assertDoesNotThrow(() -> ratingDao.deleteRating(id));
        assertFalse(ratingDao.getRatingById(id).isPresent());
    }

    @Test
    void deleteRating_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> ratingDao.deleteRating(nonExistentId));
    }

    @Test
    void deleteRating_DataAccessException_OnFileError() throws DataAccessException {
        Rating rating = createTestRating();
        String id = ratingDao.createRating(rating);
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating(id));
        tempFile.setWritable(true);
    }
}