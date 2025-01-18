package services.postgres;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.postgres.PostgresRatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.services.base.RatingService;
import com.carpooling.services.impl.RatingServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RatingServicePostgresTest extends BasePostgresTest {

    private RatingService ratingService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Создаем подключение к базе данных
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        // Создаем таблицы trips и ratings
        createTripsTable();
        createRatingsTable();

        // Инициализируем DAO и сервис
        RatingDao ratingDao = new PostgresRatingDao(connection);
        ratingService = new RatingServiceImpl(ratingDao);

        // Очищаем таблицы перед каждым тестом
        connection.createStatement().execute("DELETE FROM ratings");
        connection.createStatement().execute("DELETE FROM trips");
    }

    private void createTripsTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS trips (
            id UUID PRIMARY KEY,
            departure_time TIMESTAMP NOT NULL,
            max_passengers SMALLINT NOT NULL,
            creation_date TIMESTAMP NOT NULL,
            status VARCHAR(50) NOT NULL,
            editable BOOLEAN NOT NULL,
            user_id UUID,
            route_id UUID
        )
        """;
        connection.createStatement().execute(sql);
    }

    private void createRatingsTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS ratings (
            id UUID PRIMARY KEY,
            rating SMALLINT NOT NULL,
            comment TEXT,
            date TIMESTAMP NOT NULL,
            trip_id UUID REFERENCES trips(id)
        )
        """;
        connection.createStatement().execute(sql);
    }
    @NotNull
    private UUID createTestTrip() throws SQLException {
        String sql = """
        INSERT INTO trips (id, departure_time, max_passengers, creation_date, status, editable, user_id, route_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();  // Создаем случайный UUID для пользователя
        UUID routeId = UUID.randomUUID(); // Создаем случайный UUID для маршрута

        try (var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, tripId);
            statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setShort(3, (short) 4);
            statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setString(5, "ACTIVE");
            statement.setBoolean(6, true);
            statement.setObject(7, userId);
            statement.setObject(8, routeId);
            statement.executeUpdate();
        }
        return tripId;
    }

    @Test
    void testCreateRatingSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));

        UUID ratingId = UUID.fromString(ratingService.createRating(rating, tripId.toString()));
        assertNotNull(ratingId);

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId.toString());
        assertTrue(foundRating.isPresent());
        assertEquals(5, foundRating.get().getRating());
    }

    @Test
    void testCreateRatingFail() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        Rating rating = new Rating();
        rating.setRating(6);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));

        assertThrows(RatingServiceException.class, () -> ratingService.createRating(null, tripId.toString()));
    }

    @Test
    void testGetRatingByIdSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));

        UUID ratingId = UUID.fromString(ratingService.createRating(rating, tripId.toString()));

        Optional<Rating> foundRating = ratingService.getRatingById(ratingId.toString());
        assertTrue(foundRating.isPresent());
        assertEquals(5, foundRating.get().getRating());
    }



    @Test
    void testGetRatingByIdNotFound() throws RatingServiceException {
        assertTrue(ratingService.getRatingById(UUID.randomUUID().toString()).isEmpty());
    }
    @Test
    void testUpdateRatingSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));

        UUID ratingId = UUID.fromString(ratingService.createRating(rating, tripId.toString()));

        // Обновляем оценку
        rating.setId(ratingId.toString());
        rating.setRating(4);
        ratingService.updateRating(rating, tripId.toString());

        Optional<Rating> updatedRating = ratingService.getRatingById(ratingId.toString());
        assertTrue(updatedRating.isPresent());
        assertEquals(4, updatedRating.get().getRating());
    }

    @Test
    void testUpdateRatingFail() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setId(UUID.randomUUID().toString());
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));
        rating.setRating(6);
        assertThrows(RatingServiceException.class, () -> ratingService.updateRating(rating, tripId.toString()));
    }

    @Test
    void testDeleteRatingSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));

        UUID ratingId = UUID.fromString(ratingService.createRating(rating, tripId.toString()));
        ratingService.deleteRating(ratingId.toString());

        assertTrue(ratingService.getRatingById(ratingId.toString()).isEmpty());
    }

    @Test
    void testDeleteRatingFail() {
        assertThrows(RatingServiceException.class, () -> ratingService.deleteRating(UUID.randomUUID().toString()));
    }

    @Test
    void testGetRatingsByTripSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем две оценки для поездки
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-12-01"));
        ratingService.createRating(rating1, tripId.toString());

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка!");
        rating2.setDate(Date.valueOf("2023-12-02"));
        ratingService.createRating(rating2, tripId.toString());

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip(tripId.toString()));

    }

    @Test
    void testGetRatingsByTripNotFound() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByTrip(UUID.randomUUID().toString()));
    }


    @Test
    void testGetAverageRatingForTripSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем две оценки для поездки
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-12-01"));
        ratingService.createRating(rating1, tripId.toString());

        Rating rating2 = new Rating();
        rating2.setRating(4);
        rating2.setComment("Хорошая поездка!");
        rating2.setDate(Date.valueOf("2023-12-02"));
        ratingService.createRating(rating2, tripId.toString());

        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip(tripId.toString()));

    }

    @Test
    void testGetAverageRatingForTripNotFound() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getAverageRatingForTrip(UUID.randomUUID().toString()));
    }


    @Test
    void testGetRatingsByRatingSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем две оценки с рейтингом 5
        Rating rating1 = new Rating();
        rating1.setRating(5);
        rating1.setComment("Отличная поездка!");
        rating1.setDate(Date.valueOf("2023-12-01"));
        ratingService.createRating(rating1, tripId.toString());

        Rating rating2 = new Rating();
        rating2.setRating(5);
        rating2.setComment("Супер поездка!");
        rating2.setDate(Date.valueOf("2023-12-02"));
        ratingService.createRating(rating2, tripId.toString());

        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(5));


    }

    @Test
    void testGetRatingsByRatingNotFound() throws RatingServiceException {
        assertThrows(RatingServiceException.class, () -> ratingService.getRatingsByRating(5));
    }


    @Test
    void testGetAllRatingsSuccess() throws RatingServiceException, SQLException {
        // Создаем поездку
        UUID tripId = createTestTrip();

        // Создаем оценку
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setComment("Отличная поездка!");
        rating.setDate(Date.valueOf("2023-12-01"));
        ratingService.createRating(rating, tripId.toString());

        List<Rating> ratings = ratingService.getAllRatings();
        assertTrue(ratings.isEmpty());
    }

    @Test
    void testGetAllRatingsNotFound() throws RatingServiceException {
        List<Rating> ratings = ratingService.getAllRatings();
        assertTrue(ratings.isEmpty());
    }
}