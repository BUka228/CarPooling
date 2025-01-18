package services.mongo;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.mongo.MongoRatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.services.base.RatingService;
import com.carpooling.services.impl.RatingServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceMongoTest extends BaseMongoTest {

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        // Инициализируем DAO и сервис
        RatingDao ratingDao = new MongoRatingDao(database.getCollection("ratings"));
        ratingService = new RatingServiceImpl(ratingDao);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("ratings").drop();
    }

    @Test
    void testCreateRating_Success() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-11-15"));

        String tripId = new ObjectId().toHexString();
        String ratingId = ratingService.createRating(rating, tripId);
        assertNotNull(ratingId);

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals(5, foundRating.get().getRating());
    }

    @Test
    void testCreateRating_Failure_InvalidTripId() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-11-15"));

        assertThrows(RatingServiceException.class, () ->
                ratingService.createRating(rating, "invalid-trip-id")
        );
    }

    @Test
    void testGetRatingById_Success() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-11-15"));

        String tripId = new ObjectId().toHexString();
        String ratingId = ratingService.createRating(rating, tripId);

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals("Отличная поездка!", foundRating.get().getComment());
    }

    @Test
    void testGetRatingById_Failure_NotFound() {
        assertThrows(RatingServiceException.class, () ->
                ratingService.getRatingById("non-existent-id")
        );
    }

    @Test
    void testUpdateRating_Success() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-11-15"));

        String tripId = new ObjectId().toHexString();
        String ratingId = ratingService.createRating(rating, tripId);

        rating.setId(ratingId);
        rating.setRating(4);
        rating.setComment("Хорошая поездка, но были задержки.");

        ratingService.updateRating(rating, tripId);

        Optional<Rating> updatedRating = ratingService.getRatingById(ratingId);
        assertTrue(updatedRating.isPresent());
        assertEquals(4, updatedRating.get().getRating());
    }

    @Test
    void testUpdateRating_Failure_InvalidRatingId() {
        Rating rating = new Rating();
        rating.setId("invalid-rating-id");
        rating.setRating(4);
        rating.setComment("Хорошая поездка, но были задержки.");
        rating.setDate(Date.valueOf("2023-11-15"));

        assertThrows(RatingServiceException.class, () ->
                ratingService.updateRating(rating, "trip-1")
        );
    }

    @Test
    void testDeleteRating_Success() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-11-15"));

        String tripId = new ObjectId().toHexString();
        String ratingId = ratingService.createRating(rating, tripId);

        ratingService.deleteRating(ratingId);

        assertTrue(ratingService.getRatingById(ratingId).isEmpty());
    }

    @Test
    void testDeleteRating_Failure_InvalidRatingId() {
        assertThrows(RatingServiceException.class, () ->
                ratingService.deleteRating("invalid-rating-id")
        );
    }

    @Test
    void testGetRatingsByTrip_Success() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-11-15"));

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка, но были задержки.");
        rating2.setDate(Date.valueOf("2023-11-16"));

        String tripId = new ObjectId().toHexString();
        ratingService.createRating(rating1, tripId);
        ratingService.createRating(rating2, tripId);

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip(tripId));
    }

    @Test
    void testGetRatingsByTrip_Failure_InvalidTripId() {
        assertThrows(RatingServiceException.class, () ->
                ratingService.getRatingsByTrip("invalid-trip-id")
        );
    }

    @Test
    void testGetRatingsByRating_Success() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-11-15"));

        Rating rating2 = new Rating();
        rating2.setRating(5);
        rating2.setComment("Супер!");
        rating2.setDate(Date.valueOf("2023-11-16"));

        String tripId = new ObjectId().toHexString();
        ratingService.createRating(rating1, tripId);
        ratingService.createRating(rating2, tripId);

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(5));
    }

    @Test
    void testGetRatingsByRating_Failure_InvalidRating() {
        assertThrows(RatingServiceException.class, () ->
                ratingService.getRatingsByRating(6) // Недопустимый рейтинг
        );
    }

    @Test
    void testGetAverageRatingForTrip_Success() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-11-15"));

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка, но были задержки.");
        rating2.setDate(Date.valueOf("2023-11-16"));

        String tripId = new ObjectId().toHexString();
        ratingService.createRating(rating1, tripId);
        ratingService.createRating(rating2, tripId);

        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip(tripId));
    }

    @Test
    void testGetAverageRatingForTrip_Failure_NoRatings() {
        assertThrows(RatingServiceException.class, () ->
                ratingService.getAverageRatingForTrip("trip-1")
        );
    }
}