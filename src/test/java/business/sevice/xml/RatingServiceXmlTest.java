package business.sevice.xml;

import business.base.RatingService;
import business.service.RatingServiceImpl;
import data.dao.base.RatingDao;
import data.dao.xml.XmlRatingDao;
import data.model.database.Rating;
import exceptions.service.RatingServiceException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceXmlTest {

    private RatingService ratingService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный XML-файл
        File xmlFile = tempDir.resolve("ratings.xml").toFile();

        // Инициализируем DAO и сервис
        RatingDao ratingDao = new XmlRatingDao(xmlFile.getAbsolutePath());
        ratingService = new RatingServiceImpl(ratingDao);
    }

    @Test
    void testCreateRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String ratingId = ratingService.createRating(rating, "trip-123");
        assertNotNull(ratingId);

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals(5, foundRating.get().getRating());
    }

    @Test
    void testCreateRatingFailure() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        // Попытка создать оценку с несуществующей поездкой (должно вызвать исключение)
        assertThrows(RatingServiceException.class, () -> ratingService.createRating(null, "non-existent-trip"));
    }

    @Test
    void testGetRatingByIdSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String ratingId = ratingService.createRating(rating, "trip-123");

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals("Отличная поездка!", foundRating.get().getComment());
    }

    @Test
    void testGetRatingByIdFailure() throws RatingServiceException {
        Optional<Rating> rating = ratingService.getRatingById("non-existent-id");
        assertFalse(rating.isPresent());
    }

    @Test
    void testUpdateRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String ratingId = ratingService.createRating(rating, "trip-123");

        rating.setId(ratingId);
        rating.setComment("Поездка была хорошей, но могли бы быть лучше.");
        ratingService.updateRating(rating, "trip-123");

        Optional<Rating> updatedRating = ratingService.getRatingById(ratingId);
        assertTrue(updatedRating.isPresent());
        assertEquals("Поездка была хорошей, но могли бы быть лучше.", updatedRating.get().getComment());
    }

    @Test
    void testUpdateRatingFailure() {
        Rating rating = new Rating();
        rating.setId("non-existent-id");
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        assertThrows(RatingServiceException.class, () -> ratingService.updateRating(rating, "trip-123"));
    }

    @Test
    void testDeleteRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String ratingId = ratingService.createRating(rating, "trip-123");
        ratingService.deleteRating(ratingId);

        Optional<Rating> deletedRating = ratingService.getRatingById(ratingId);
        assertFalse(deletedRating.isPresent());
    }

    @Test
    void testDeleteRatingFailure() {
        assertThrows(RatingServiceException.class, () -> ratingService.deleteRating("non-existent-id"));
    }

    @Test
    void testGetRatingsByTripSuccess() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-10-01"));
        ratingService.createRating(rating1, "trip-123");

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка!");
        rating2.setDate(Date.valueOf("2023-10-02"));
        ratingService.createRating(rating2, "trip-123");

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip("trip-123"));

    }

    @Test
    void testGetRatingsByTripFailure() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip("non-existent-trip"));
    }

    @Test
    void testGetRatingsByRatingSuccess() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-10-01"));
        ratingService.createRating(rating1, "trip-123");

        Rating rating2 = new Rating();
        rating2.setRating(5);
        rating2.setComment("Супер поездка!");
        rating2.setDate(Date.valueOf("2023-10-02"));
        ratingService.createRating(rating2, "trip-123");

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(4));
    }

    @Test
    void testGetRatingsByRatingFailure() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(3));
    }

    @Test
    void testGetAverageRatingForTripSuccess() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-10-01"));
        ratingService.createRating(rating1, "trip-123");

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка!");
        rating2.setDate(Date.valueOf("2023-10-02"));
        ratingService.createRating(rating2, "trip-123");

        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip("trip-123"));
    }

    @Test
    void testGetAverageRatingForTripFailure() {
        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip("non-existent-trip"));
    }
}