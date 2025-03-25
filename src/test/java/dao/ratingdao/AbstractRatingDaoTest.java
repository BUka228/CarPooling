package dao.ratingdao;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public abstract class AbstractRatingDaoTest {

    protected RatingDao ratingDao;

    @BeforeEach
    public void setUp() {
        ratingDao = createRatingDao();
    }

    @AfterEach
    public void tearDown() {
        cleanUp();
    }

    protected abstract RatingDao createRatingDao();
    protected abstract void cleanUp();

    @Test
    void createRating_successful() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Great trip!");
        rating.setDate(new Date());

        String ratingId = ratingDao.createRating(rating);

        assertNotNull(ratingId, "Rating ID should not be null");
        Optional<Rating> createdRating = ratingDao.getRatingById(ratingId);
        assertTrue(createdRating.isPresent(), "Rating should be created");
        assertEquals(5, createdRating.get().getRating(), "Rating value should match");
    }

    @Test
    void createRating_withInvalidData_throwsException() {
        Rating rating = new Rating();
        rating.setRating(6); // Invalid rating (assuming max is 5)
        rating.setComment(null); // Comment cannot be null
        rating.setDate(null); // Date cannot be null

        assertThrows(DataAccessException.class, () -> ratingDao.createRating(rating),
                "Should throw an exception when creating a rating with invalid data");
    }

    @Test
    void getRatingById_successful() {
        Rating rating = new Rating();
        rating.setRating(4);
        rating.setComment("Good trip");
        rating.setDate(new Date());
        String ratingId = ratingDao.createRating(rating);

        Optional<Rating> retrievedRating = ratingDao.getRatingById(ratingId);

        assertTrue(retrievedRating.isPresent(), "Rating should be found");
        assertEquals("Good trip", retrievedRating.get().getComment(), "Comment should match");
    }

    @Test
    void getRatingById_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Not a valid UUID

        assertThrows(DataAccessException.class, () -> ratingDao.getRatingById(invalidId),
                "Should throw an exception when retrieving a rating with an invalid ID");
    }

    @Test
    void getRatingById_withNonExistingId_returnsEmpty() {
        String nonExistingId = UUID.randomUUID().toString();

        Optional<Rating> retrievedRating = ratingDao.getRatingById(nonExistingId);

        assertFalse(retrievedRating.isPresent(), "Should return empty for a non-existing ID");
    }

    @Test
    void updateRating_successful() {
        Rating rating = new Rating();
        rating.setRating(3);
        rating.setComment("Average trip");
        rating.setDate(new Date());
        String ratingId = ratingDao.createRating(rating);

        Rating updatedRating = ratingDao.getRatingById(ratingId).get();
        updatedRating.setRating(4);
        updatedRating.setComment("Better than expected");
        ratingDao.updateRating(updatedRating);

        Optional<Rating> retrievedRating = ratingDao.getRatingById(ratingId);
        assertTrue(retrievedRating.isPresent(), "Rating should exist after update");
        assertEquals(4, retrievedRating.get().getRating(), "Rating should be updated");
        assertEquals("Better than expected", retrievedRating.get().getComment(), "Comment should be updated");
    }

    @Test
    void updateRating_withNonExistingRating_throwsException() {
        Rating nonExistingRating = new Rating();
        nonExistingRating.setId(UUID.randomUUID());
        nonExistingRating.setRating(2);
        nonExistingRating.setComment("Poor trip");

        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(nonExistingRating),
                "Should throw an exception when updating a non-existing rating");
    }

    @Test
    void deleteRating_successful() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Excellent trip");
        rating.setDate(new Date());
        String ratingId = ratingDao.createRating(rating);

        ratingDao.deleteRating(ratingId);

        Optional<Rating> retrievedRating = ratingDao.getRatingById(ratingId);
        assertFalse(retrievedRating.isPresent(), "Rating should be deleted");
    }

    @Test
    void deleteRating_withNonExistingId_doesNotThrowException() {
        String nonExistingId = UUID.randomUUID().toString();

        assertDoesNotThrow(() -> ratingDao.deleteRating(nonExistingId),
                "Deleting a non-existing rating should not throw an exception");
    }

    @Test
    void deleteRating_withInvalidId_throwsException() {
        String invalidId = "invalid-id"; // Not a valid UUID

        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating(invalidId),
                "Should throw an exception when deleting a rating with an invalid ID");
    }
}