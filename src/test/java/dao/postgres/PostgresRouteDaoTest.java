package dao.postgres;


import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.base.TripDao; // Добавили импорт TripDao
import com.carpooling.dao.base.UserDao; // Добавили импорт UserDao
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.Address; // Для хелпера User
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;  // Добавили импорт Trip
import com.carpooling.entities.database.User;  // Добавили импорт User
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.hibernate.ThreadLocalSessionContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException; // Для теста удаления
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate; // Для хелпера User
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class PostgresRouteDaoTest {

    private static SessionFactory sessionFactory;
    private RouteDao routeDao; // Тестируемый DAO
    // --- Добавляем DAO для зависимостей ---
    private UserDao userDao;
    private TripDao tripDao;
    // ------------------------------------
    private Session session;
    private Transaction transaction;

    // --- Инициализация и Завершение ---
    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() {}

    @BeforeEach
    void setUp() {
        // --- Инициализируем все DAO ---
        userDao = new PostgresUserDao(sessionFactory);
        tripDao = new PostgresTripDao(sessionFactory);
        routeDao = new PostgresRouteDao(sessionFactory);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();
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
    private User buildUser(String emailSuffix) {
        User user = new User();
        user.setName("RouteTest User " + emailSuffix);
        user.setEmail("routeuser." + emailSuffix + "@test.com");
        user.setPassword("passroute");
        user.setBirthDate(LocalDate.of(1992, 8, 15));
        user.setAddress(new Address("Route St", "RT1", "RouteCity"));
        return user;
    }

    private Route buildRoute(String start, String end) {
        Route route = new Route();
        route.setStartingPoint(start);
        route.setEndingPoint(end);
        route.setDate(LocalDateTime.now().plusHours(1));
        route.setEstimatedDuration((short) 60);
        return route;
    }

    private Trip buildTrip(User user, Route route, LocalDateTime departure) {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setRoute(route);
        trip.setDepartureTime(departure);
        trip.setMaxPassengers((byte) 3);
        trip.setStatus(com.carpooling.entities.enums.TripStatus.PLANNED);
        trip.setEditable(true);
        return trip;
    }

    // Хелпер для создания и сохранения User через DAO
    private User createAndPersistTestUser(String emailSuffix) throws DataAccessException {
        User user = buildUser(emailSuffix);
        userDao.createUser(user);
        session.flush();
        return user;
    }

    // Хелпер для создания и сохранения Route через DAO
    private Route createAndPersistTestRoute(String start, String end) throws DataAccessException {
        Route route = buildRoute(start, end);
        routeDao.createRoute(route);
        session.flush();
        session.clear(); // Очищаем, чтобы вернуть detached объект с ID
        // Загружаем снова, чтобы получить ID, если он не установился в объект при persist
        Route persistedRoute = session.get(Route.class, route.getId());
        return persistedRoute != null ? persistedRoute : route;
    }

    // Хелпер для создания и сохранения Trip через DAO
    private Trip createAndPersistTestTrip(User user, Route route, LocalDateTime departure) throws DataAccessException {
        Trip trip = buildTrip(user, route, departure);
        tripDao.createTrip(trip);
        session.flush();
        session.clear();
        Trip persistedTrip = session.get(Trip.class, trip.getId());
        return persistedTrip != null ? persistedTrip : trip;
    }

    // ================== Тесты createRoute ==================

    @Test
    void createRoute_Success_ShouldPersistAndReturnId() throws DataAccessException {
        // Arrange
        Route route = buildRoute("Point A", "Point B");

        // Act
        String routeIdStr = routeDao.createRoute(route);
        UUID generatedId = route.getId();
        session.flush();

        // Assert
        assertThat(generatedId).isNotNull();
        assertThat(routeIdStr).isEqualTo(generatedId.toString());

        session.clear();
        Route foundRoute = session.get(Route.class, generatedId);
        assertThat(foundRoute).isNotNull();
        assertThat(foundRoute.getStartingPoint()).isEqualTo("Point A");
        assertThat(foundRoute.getEndingPoint()).isEqualTo("Point B");
    }

    @Test
    void createRoute_Failure_NullStartingPoint_ShouldThrowException() {
        // Arrange
        Route route = buildRoute(null, "Point B");

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            routeDao.createRoute(route);
            session.flush();
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Route.startingPoint");
    }

    @Test
    void createRoute_Failure_NullEndingPoint_ShouldThrowException() {
        // Arrange
        Route route = buildRoute("Point A", null);

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            routeDao.createRoute(route);
            session.flush();
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Route.endingPoint");
    }

    // ================== Тесты getRouteById ==================

    @Test
    void getRouteById_Success_WhenExists_ShouldReturnRoute() throws DataAccessException {
        // Arrange
        Route persistedRoute = createAndPersistTestRoute("Point C", "Point D");
        String routeId = persistedRoute.getId().toString();
        session.clear();

        // Act
        Optional<Route> foundOpt = routeDao.getRouteById(routeId);

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedRoute.getId());
        assertThat(foundOpt.get().getStartingPoint()).isEqualTo("Point C");
    }

    @Test
    void getRouteById_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act
        Optional<Route> foundOpt = routeDao.getRouteById(nonExistentId);
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void getRouteById_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "route-123";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            routeDao.getRouteById(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for route id"); // Проверяем сообщение из parseUUID
    }

    // ================== Тесты updateRoute ==================

    @Test
    void updateRoute_Success_ShouldUpdateFields() throws DataAccessException {
        // Arrange
        Route persistedRoute = createAndPersistTestRoute("Point E", "Point F");
        UUID routeId = persistedRoute.getId();
        session.clear();

        Route routeToUpdate = session.get(Route.class, routeId);
        assertThat(routeToUpdate).isNotNull();
        routeToUpdate.setStartingPoint("Point E Updated");
        routeToUpdate.setEstimatedDuration((short) 90);
        LocalDateTime newDate = LocalDateTime.now().plusDays(10);
        routeToUpdate.setDate(newDate);

        // Act
        routeDao.updateRoute(routeToUpdate); // merge
        session.flush();
        session.clear();

        // Assert
        Route updatedRoute = session.get(Route.class, routeId);
        assertThat(updatedRoute).isNotNull();
        assertThat(updatedRoute.getStartingPoint()).isEqualTo("Point E Updated");
        assertThat(updatedRoute.getEndingPoint()).isEqualTo("Point F");
        assertThat(updatedRoute.getEstimatedDuration()).isEqualTo((short) 90);
    }

    @Test
    void updateRoute_Failure_SetNullRequiredField_ShouldThrowException() throws DataAccessException {
        // Arrange
        Route persistedRoute = createAndPersistTestRoute("Point G", "Point H");
        UUID routeId = persistedRoute.getId();
        session.clear();

        Route routeToUpdate = session.get(Route.class, routeId);
        assertThat(routeToUpdate).isNotNull();
        routeToUpdate.setEndingPoint(null); // Нарушаем not-null

        // Act & Assert
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            routeDao.updateRoute(routeToUpdate); // merge
            session.flush(); // Ошибка PropertyValueException
        });

        Throwable rootCause = ex;
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Route.endingPoint");
    }

    // ================== Тесты deleteRoute ==================

    @Test
    void deleteRoute_Success_WhenExists_ShouldRemoveRoute() throws DataAccessException {
        // Arrange
        Route persistedRoute = createAndPersistTestRoute("Point I", "Point J");
        String routeId = persistedRoute.getId().toString();
        UUID routeUUID = persistedRoute.getId();

        // Act
        routeDao.deleteRoute(routeId);
        session.flush();

        // Assert
        session.clear();
        Route deletedRoute = session.get(Route.class, routeUUID);
        assertThat(deletedRoute).isNull();
    }

    @Test
    void deleteRoute_Failure_WhenNotExists_ShouldDoNothing() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act & Assert
        assertDoesNotThrow(() -> {
            routeDao.deleteRoute(nonExistentId);
        });
        assertThat(session.get(Route.class, UUID.fromString(nonExistentId))).isNull();
    }

    @Test
    void deleteRoute_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "route-invalid";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            routeDao.deleteRoute(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for route id"); // Проверяем сообщение из parseUUID
    }

    @Test
    void deleteRoute_Failure_WhenReferencedByTrip_ShouldThrowException() throws DataAccessException {
        // Arrange
        User user = createAndPersistTestUser("delRouteFail");
        Route routeToDelete = createAndPersistTestRoute("DeleteMe", "IfYouCan");
        Trip trip = buildTrip(user, routeToDelete, LocalDateTime.now().plusDays(3));
        tripDao.createTrip(trip);
        session.flush();
        session.clear();

        String routeId = routeToDelete.getId().toString();

        // Act & Assert: Ожидаем PersistenceException (или ConstraintViolationException)
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            routeDao.deleteRoute(routeId); // DAO помечает для удаления
            session.flush(); // Ошибка ConstraintViolationException при flush
        });

        // Проверяем причину
        Throwable cause = ex;
        while (cause != null && !(cause instanceof ConstraintViolationException)) {
            cause = cause.getCause();
        }
        assertThat(cause).isInstanceOf(ConstraintViolationException.class);
        // Опционально: проверить SQLState или имя констрейнта
        SQLException sqlEx = ((ConstraintViolationException)cause).getSQLException();
        assertThat(sqlEx.getSQLState()).isEqualTo("23503"); // FK violation в H2 (и PostgreSQL)
    }
}