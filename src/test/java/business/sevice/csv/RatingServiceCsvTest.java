package business.sevice.csv;

import business.base.RatingService;
import business.service.RatingServiceImpl;
import data.dao.base.RatingDao;
import data.dao.csv.CsvRatingDao;
import data.model.database.Rating;
import exceptions.service.RatingServiceException;
import exceptions.service.RouteServiceException;
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

class RatingServiceCsvTest {

    private RatingService ratingService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный CSV-файл
        File csvFile = tempDir.resolve("ratings.csv").toFile();

        // Инициализируем DAO и сервис
        RatingDao ratingDao = new CsvRatingDao(csvFile.getAbsolutePath());
        ratingService = new RatingServiceImpl(ratingDao);
    }

    @Test
    void testCreateRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String tripId = "trip-123";
        String ratingId = ratingService.createRating(rating, tripId);
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

        String tripId = "trip-123";

        // Успешное создание оценки
        assertDoesNotThrow(() -> ratingService.createRating(rating, tripId));

        assertThrows(RatingServiceException.class, () -> ratingService.createRating(null, tripId));
    }

    @Test
    void testGetRatingByIdSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(4);
        rating.setComment("Хорошая поездка");
        rating.setDate(Date.valueOf("2023-10-02"));

        String tripId = "trip-456";
        String ratingId = ratingService.createRating(rating, tripId);

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId);
        assertTrue(foundRating.isPresent());
        assertEquals(4, foundRating.get().getRating());
    }

    @Test
    void testGetRatingByIdFailure() throws RatingServiceException {
        Optional<Rating> rating = ratingService.getRatingById("non-existent-id");
        assertFalse(rating.isPresent());
    }

    @Test
    void testUpdateRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(3);
        rating.setComment("Нормально");
        rating.setDate(Date.valueOf("2023-10-03"));

        String tripId = "trip-789";
        String ratingId = ratingService.createRating(rating, tripId);

        rating.setId(ratingId);
        rating.setRating(4);
        rating.setComment("Стало лучше");
        ratingService.updateRating(rating, tripId);

        Optional<Rating> updatedRating = ratingService.getRatingById(ratingId);
        assertTrue(updatedRating.isPresent());
        assertEquals(4, updatedRating.get().getRating());
    }

    @Test
    void testUpdateRatingFailure() {
        Rating rating = new Rating();
        rating.setId("non-existent-id");
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String tripId = "trip-123";
        assertThrows(RatingServiceException.class, () -> ratingService.updateRating(rating, tripId));
    }

    @Test
    void testDeleteRatingSuccess() throws RatingServiceException {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-10-01"));

        String tripId = "trip-123";
        String ratingId = ratingService.createRating(rating, tripId);
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

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка");
        rating2.setDate(Date.valueOf("2023-10-02"));

        String tripId = "trip-123";
        ratingService.createRating(rating1, tripId);
        ratingService.createRating(rating2, tripId);

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip("trip-123"));
    }

    @Test
    void testGetRatingsByTripFailure() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip("non-existent-trip-id"));
    }

    @Test
    void testGetRatingsByRatingSuccess() throws RatingServiceException {
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-10-01"));

        Rating rating2 = new Rating();
        rating2.setRating(5);
        rating2.setComment("Еще одна отличная поездка");
        rating2.setDate(Date.valueOf("2023-10-02"));

        String tripId1 = "trip-123";
        String tripId2 = "trip-456";
        ratingService.createRating(rating1, tripId1);
        ratingService.createRating(rating2, tripId2);

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(5));
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

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка");
        rating2.setDate(Date.valueOf("2023-10-02"));

        String tripId = "trip-123";
        ratingService.createRating(rating1, tripId);
        ratingService.createRating(rating2, tripId);

        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip("trip-123"));
    }

    @Test
    void testGetAverageRatingForTripFailure() {
        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip("non-existent-trip-id"));
    }
}