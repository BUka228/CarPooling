package dao.postgres;

import com.carpooling.dao.base.*; // Импорт всех базовых DAO
import com.carpooling.dao.postgres.PostgresRatingDao;
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.*; // Импорт всех сущностей
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException; // Для find*
import com.carpooling.hibernate.ThreadLocalSessionContext;

import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class PostgresRatingDaoTest {

    private static SessionFactory sessionFactory;
    // DAO для зависимостей
    private UserDao userDao;
    private RouteDao routeDao;
    private TripDao tripDao;
    // Тестируемый DAO
    private RatingDao ratingDao;

    private Session session;
    private Transaction transaction;

    // Тестовые данные
    private User testUser1;
    private Trip testTrip1;
    private Trip testTrip2;

    // --- Инициализация и Завершение ---
    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() {}

    @BeforeEach
    void setUp() throws DataAccessException {
        // Создаем DAO
        userDao = new PostgresUserDao(sessionFactory);
        routeDao = new PostgresRouteDao(sessionFactory);
        tripDao = new PostgresTripDao(sessionFactory);
        ratingDao = new PostgresRatingDao(sessionFactory);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();

        // Создаем базовые данные
        createTestData();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) { System.err.println("Error rolling back: " + e.getMessage()); }
        finally {
            try { ThreadLocalSessionContext.unbind(); }
            catch (Exception e) { System.err.println("Error unbinding: " + e.getMessage()); }
            finally {
                if (session != null && session.isOpen()) { session.close(); }
            }
        }
    }

    // --- Хелперы ---

    private User createAndPersistTestUser(String emailSuffix) throws DataAccessException {
        User user = new User();
        user.setName("Rate Test User " + emailSuffix);
        user.setEmail("rateuser." + emailSuffix + "@test.com");
        user.setPassword("pass123");
        user.setBirthDate(LocalDate.of(1998, 6, 20));
        userDao.createUser(user);
        session.flush();
        return user;
    }

    private Route createAndPersistTestRoute(String start, String end) throws DataAccessException {
        Route route = new Route();
        route.setStartingPoint(start);
        route.setEndingPoint(end);
        routeDao.createRoute(route);
        session.flush();
        return route;
    }

    private Trip createAndPersistTestTrip(User user, Route route) throws DataAccessException {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setRoute(route);
        trip.setDepartureTime(LocalDateTime.now().plusDays(1));
        trip.setMaxPassengers((byte) 2);
        trip.setStatus(com.carpooling.entities.enums.TripStatus.COMPLETED); // Завершенная для оценок
        tripDao.createTrip(trip);
        session.flush();
        return trip;
    }

    private Rating buildRating(Trip trip, int ratingValue, String comment) {
        Rating rating = new Rating();
        rating.setTrip(trip);
        rating.setRating(ratingValue);
        rating.setComment(comment);
        rating.setDate(LocalDateTime.now().minusMinutes(5)); // Дата в прошлом
        // Связь с User устанавливается через Trip в PostgresRatingDao.findRatingByUserAndTrip
        return rating;
    }

    private Rating createAndPersistTestRating(Trip trip, int ratingValue, String comment) throws DataAccessException {
        Rating rating = buildRating(trip, ratingValue, comment);
        ratingDao.createRating(rating);
        session.flush();
        session.clear();
        return rating;
    }

    private void createTestData() throws DataAccessException {
        testUser1 = createAndPersistTestUser("1");
        User testUser2 = createAndPersistTestUser("2"); // Для других поездок/оценок
        Route route1 = createAndPersistTestRoute("PointR1", "PointR2");
        Route route2 = createAndPersistTestRoute("PointR3", "PointR4");
        testTrip1 = createAndPersistTestTrip(testUser1, route1);
        testTrip2 = createAndPersistTestTrip(testUser2, route2);
        session.clear();
    }

    // ================== Тесты createRating ==================

    @Test
    void createRating_Success_ShouldPersistAndReturnId() throws DataAccessException {
        // Arrange
        Rating rating = buildRating(testTrip1, 5, "Excellent!");

        // Act
        String ratingIdStr = ratingDao.createRating(rating);
        UUID generatedId = rating.getId();
        session.flush();

        // Assert
        assertThat(generatedId).isNotNull();
        assertThat(ratingIdStr).isEqualTo(generatedId.toString());

        session.clear();
        Rating foundRating = session.get(Rating.class, generatedId);
        assertThat(foundRating).isNotNull();
        assertThat(foundRating.getTrip().getId()).isEqualTo(testTrip1.getId());
        assertThat(foundRating.getRating()).isEqualTo(5);
        assertThat(foundRating.getComment()).isEqualTo("Excellent!");
        assertThat(foundRating.getDate()).isNotNull();
    }

    @Test
    void createRating_Failure_NullTrip_ShouldThrowException() {
        // Arrange
        Rating rating = buildRating(null, 4, "Trip was null"); // Trip = null (нарушение not-null FK)

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.createRating(rating);
            session.flush(); // Ошибка PropertyValueException
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Rating.trip");
    }

    // Тест на null rating или date тоже можно добавить, если они not-null
    @Test
    void createRating_Failure_NullDate_ShouldThrowException() {
        // Arrange
        Rating rating = buildRating(testTrip1, 3, null);
        rating.setDate(null); // Нарушаем not-null

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.createRating(rating);
            session.flush();
        });
        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Rating.date");
    }

    // ================== Тесты getRatingById ==================

    @Test
    void getRatingById_Success_WhenExists_ShouldReturnRating() throws DataAccessException {
        // Arrange
        Rating persistedRating = createAndPersistTestRating(testTrip1, 4, "Good");
        String ratingId = persistedRating.getId().toString();
        session.clear();

        // Act
        Optional<Rating> foundOpt = ratingDao.getRatingById(ratingId);

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedRating.getId());
        assertThat(foundOpt.get().getRating()).isEqualTo(4);
        assertThat(foundOpt.get().getComment()).isEqualTo("Good");
    }

    @Test
    void getRatingById_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act
        Optional<Rating> foundOpt = ratingDao.getRatingById(nonExistentId);
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void getRatingById_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-rating-id";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.getRatingById(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    // ================== Тесты updateRating ==================

    @Test
    void updateRating_Success_ShouldUpdateFields() throws DataAccessException {
        // Arrange
        Rating persistedRating = createAndPersistTestRating(testTrip1, 2, "Bad");
        UUID ratingId = persistedRating.getId();
        session.clear();

        Rating ratingToUpdate = session.get(Rating.class, ratingId);
        assertThat(ratingToUpdate).isNotNull();
        ratingToUpdate.setRating(1);
        ratingToUpdate.setComment("Very Bad!");
        ratingToUpdate.setDate(LocalDateTime.now()); // Обновляем дату

        // Act
        ratingDao.updateRating(ratingToUpdate); // merge
        session.flush();
        session.clear();

        // Assert
        Rating updatedRating = session.get(Rating.class, ratingId);
        assertThat(updatedRating).isNotNull();
        assertThat(updatedRating.getRating()).isEqualTo(1);
        assertThat(updatedRating.getComment()).isEqualTo("Very Bad!");
        assertThat(updatedRating.getDate()).isAfter(persistedRating.getDate()); // Проверяем обновление даты
    }

    @Test
    void updateRating_Failure_SetNullRequiredField_ShouldThrowException() throws DataAccessException {
        // Arrange
        Rating persistedRating = createAndPersistTestRating(testTrip2, 5, "OK");
        UUID ratingId = persistedRating.getId();
        session.clear();

        Rating ratingToUpdate = session.get(Rating.class, ratingId);
        assertThat(ratingToUpdate).isNotNull();
        ratingToUpdate.setDate(null); // Нарушаем not-null

        // Act & Assert
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            ratingDao.updateRating(ratingToUpdate); // merge
            session.flush(); // Ошибка PropertyValueException
        });

        Throwable rootCause = ex;
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Rating.date");
    }


    // ================== Тесты deleteRating ==================

    @Test
    void deleteRating_Success_WhenExists_ShouldRemoveRating() throws DataAccessException {
        // Arrange
        Rating persistedRating = createAndPersistTestRating(testTrip1, 3, "So-so");
        String ratingId = persistedRating.getId().toString();
        UUID ratingUUID = persistedRating.getId();

        // Act
        ratingDao.deleteRating(ratingId);
        session.flush();

        // Assert
        session.clear();
        Rating deletedRating = session.get(Rating.class, ratingUUID);
        assertThat(deletedRating).isNull();
    }

    @Test
    void deleteRating_Failure_WhenNotExists_ShouldDoNothing() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act & Assert
        assertDoesNotThrow(() -> {
            ratingDao.deleteRating(nonExistentId);
        });
        assertThat(session.get(Rating.class, UUID.fromString(nonExistentId))).isNull();
    }

    @Test
    void deleteRating_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "id-rating-bad";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.deleteRating(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }


    // ================== Тесты findRatingsByTripId ==================

    @Test
    void findRatingsByTripId_Success_WhenRatingsExist_ShouldReturnList() throws DataAccessException, OperationNotSupportedException {
        // Arrange
        Rating rating1 = createAndPersistTestRating(testTrip1, 5, "Great");
        Rating rating2 = createAndPersistTestRating(testTrip1, 4, "Good");
        // Оценка для другой поездки
        createAndPersistTestRating(testTrip2, 1, "Awful");
        session.clear();

        // Act
        List<Rating> ratings = ratingDao.findRatingsByTripId(testTrip1.getId().toString());

        // Assert
        assertThat(ratings)
                .isNotNull()
                .hasSize(2)
                .extracting(Rating::getId)
                .containsExactlyInAnyOrder(rating1.getId(), rating2.getId());
    }

    @Test
    void findRatingsByTripId_Success_WhenNoRatings_ShouldReturnEmptyList() throws DataAccessException, OperationNotSupportedException {
        // Arrange: testTrip2 не имеет оценок

        // Act
        List<Rating> ratings = ratingDao.findRatingsByTripId(testTrip2.getId().toString());

        // Assert
        assertThat(ratings).isNotNull().isEmpty();
    }

    @Test
    void findRatingsByTripId_Failure_InvalidTripIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "trip-id-invalid";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.findRatingsByTripId(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }


    // ================== Тесты findRatingByUserAndTrip ==================
    // Эти тесты предполагают, что у Rating ЕСТЬ связь с User

    @Test
    void findRatingByUserAndTrip_Success_WhenExists_ShouldReturnRating() throws DataAccessException, OperationNotSupportedException {
        // Arrange
        // Создаем рейтинг
        Rating persistedRating = createAndPersistTestRating(testTrip1, 4, "Comment");
        // Предположим, что HQL загружает User из Trip
        User userWhoRated = testTrip1.getUser();
        session.clear();

        // Act
        Optional<Rating> foundOpt = ratingDao.findRatingByUserAndTrip(userWhoRated.getId().toString(), testTrip1.getId().toString());

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedRating.getId());
        // HQL должен был загрузить User и Trip
        assertThat(foundOpt.get().getTrip()).isNotNull();
        //assertThat(foundOpt.get().getUser()).isNotNull(); // Если есть связь user в Rating
    }

    @Test
    void findRatingByUserAndTrip_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException, OperationNotSupportedException {
        // Arrange: User1 не оценивал Trip2
        // Act
        Optional<Rating> foundOpt = ratingDao.findRatingByUserAndTrip(testUser1.getId().toString(), testTrip2.getId().toString());
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void findRatingByUserAndTrip_Failure_InvalidUserIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-u";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.findRatingByUserAndTrip(invalidId, testTrip1.getId().toString());
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for user ID");
    }

    @Test
    void findRatingByUserAndTrip_Failure_InvalidTripIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-t";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ratingDao.findRatingByUserAndTrip(testUser1.getId().toString(), invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for trip ID"); // Или "user or trip ID"
    }

}