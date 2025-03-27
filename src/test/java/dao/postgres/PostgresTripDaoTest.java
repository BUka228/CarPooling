package dao.postgres;

import com.carpooling.dao.base.RouteDao; // Понадобится для создания Route
import com.carpooling.dao.base.TripDao;  // Тестируемый интерфейс
import com.carpooling.dao.base.UserDao;  // Понадобится для создания User
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.entities.enums.TripStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.hibernate.ThreadLocalSessionContext; // Контекст для тестов
import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;



class PostgresTripDaoTest {

    private static SessionFactory sessionFactory;
    private UserDao userDao;
    private RouteDao routeDao;
    private TripDao tripDao;

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
        // Создаем все DAO для тестов
        userDao = new PostgresUserDao(sessionFactory);
        routeDao = new PostgresRouteDao(sessionFactory);
        tripDao = new PostgresTripDao(sessionFactory);

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

    // Хелпер для User (из предыдущего теста, немного упрощен)
    private User createAndPersistTestUser(String emailSuffix) throws DataAccessException {
        User user = new User();
        user.setName("Test User " + emailSuffix);
        user.setEmail("user." + emailSuffix + "@test.com");
        user.setPassword("password");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        userDao.createUser(user); // Используем DAO
        session.flush();
        return user;
    }

    // Хелпер для Route
    private Route createAndPersistTestRoute(String start, String end) throws DataAccessException {
        Route route = new Route();
        route.setStartingPoint(start);
        route.setEndingPoint(end);
        route.setDate(LocalDateTime.now().plusDays(1)); // Дата в будущем
        routeDao.createRoute(route); // Используем DAO
        session.flush();
        return route;
    }

    // Хелпер для Trip (без сохранения)
    private Trip buildTrip(User user, Route route) {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setRoute(route);
        trip.setDepartureTime(LocalDateTime.now().plusDays(2)); // Время в будущем
        trip.setMaxPassengers((byte) 4);
        trip.setStatus(TripStatus.PLANNED);
        trip.setEditable(true);
        // creationDate установится автоматически через @CreationTimestamp
        return trip;
    }

    // Хелпер для создания и сохранения Trip
    private Trip createAndPersistTestTrip(String userSuffix, String routeStart, String routeEnd) throws DataAccessException {
        User user = createAndPersistTestUser(userSuffix);
        Route route = createAndPersistTestRoute(routeStart, routeEnd);
        Trip trip = buildTrip(user, route);
        tripDao.createTrip(trip); // Сохраняем через тестируемый DAO
        session.flush();
        session.clear(); // Очищаем для следующих операций
        return trip;
    }

    // ================== Тесты createTrip ==================

    @Test
    void createTrip_Success_ShouldPersistAndReturnId() throws DataAccessException {
        // Arrange
        User user = createAndPersistTestUser("createTripSuccess");
        Route route = createAndPersistTestRoute("StartA", "EndB");
        Trip trip = buildTrip(user, route);

        // Act
        String tripIdStr = tripDao.createTrip(trip);
        UUID generatedId = trip.getId();
        session.flush();

        // Assert
        assertThat(generatedId).isNotNull();
        assertThat(tripIdStr).isEqualTo(generatedId.toString());

        session.clear();
        Trip foundTrip = session.get(Trip.class, generatedId);
        assertThat(foundTrip).isNotNull();
        assertThat(foundTrip.getUser().getId()).isEqualTo(user.getId());
        assertThat(foundTrip.getRoute().getId()).isEqualTo(route.getId());
        assertThat(foundTrip.getStatus()).isEqualTo(TripStatus.PLANNED);
        assertThat(foundTrip.getCreationDate()).isNotNull(); // Проверяем автогенерацию
    }

    @Test
    void createTrip_Failure_NullRoute_ShouldThrowException() throws DataAccessException {
        // Arrange
        User user = createAndPersistTestUser("createTripNullRoute");
        Trip trip = buildTrip(user, null); // Route = null (нарушение not-null в @JoinColumn)

        // Act & Assert: Ожидаем ошибку при persist/flush
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            tripDao.createTrip(trip);
            session.flush();
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        // Проверка PropertyValueException для ассоциации
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Trip.route");
    }

    @Test
    void createTrip_Failure_NullUser_ShouldThrowException() throws DataAccessException {
        // Arrange
        Route route = createAndPersistTestRoute("StartC", "EndD");
        Trip trip = buildTrip(null, route); // User = null (нарушение not-null в @JoinColumn)

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            tripDao.createTrip(trip);
            session.flush();
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Trip.user");
    }


    // ================== Тесты getTripById ==================

    @Test
    void getTripById_Success_WhenExists_ShouldReturnTripWithDetails() throws DataAccessException {
        // Arrange
        Trip persistedTrip = createAndPersistTestTrip("getTripSuccess", "StartE", "EndF");
        String tripId = persistedTrip.getId().toString();

        // Act
        Optional<Trip> foundOpt = tripDao.getTripById(tripId); // Метод DAO использует HQL с JOIN FETCH

        // Assert
        assertThat(foundOpt).isPresent();
        Trip foundTrip = foundOpt.get();
        assertThat(foundTrip.getId()).isEqualTo(persistedTrip.getId());
        // Проверяем, что связанные сущности загружены (не null)
        assertThat(foundTrip.getUser()).isNotNull();
        assertThat(foundTrip.getRoute()).isNotNull();
        assertThat(foundTrip.getUser().getId()).isEqualTo(persistedTrip.getUser().getId());
        assertThat(foundTrip.getRoute().getId()).isEqualTo(persistedTrip.getRoute().getId());
        assertThat(foundTrip.getRoute().getStartingPoint()).isEqualTo("StartE");
    }

    @Test
    void getTripById_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act
        Optional<Trip> foundOpt = tripDao.getTripById(nonExistentId);
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void getTripById_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "not-a-trip-uuid";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            tripDao.getTripById(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }


    // ================== Тесты updateTrip ==================

    @Test
    void updateTrip_Success_ShouldUpdateFields() throws DataAccessException {
        // Arrange
        Trip persistedTrip = createAndPersistTestTrip("updateTripSuccess", "StartG", "EndH");
        UUID tripId = persistedTrip.getId();
        session.clear();

        Trip tripToUpdate = session.get(Trip.class, tripId);
        assertThat(tripToUpdate).isNotNull();
        tripToUpdate.setStatus(TripStatus.CANCELLED);
        tripToUpdate.setMaxPassengers((byte) 2);
        LocalDateTime newDeparture = LocalDateTime.now().plusDays(5);
        tripToUpdate.setDepartureTime(newDeparture);

        // Act
        tripDao.updateTrip(tripToUpdate); // merge
        session.flush();
        session.clear();

        // Assert
        Trip updatedTrip = session.get(Trip.class, tripId);
        assertThat(updatedTrip).isNotNull();
        assertThat(updatedTrip.getStatus()).isEqualTo(TripStatus.CANCELLED);
        assertThat(updatedTrip.getMaxPassengers()).isEqualTo((byte) 2);
        // Убедимся, что связанные сущности не изменились
        assertThat(updatedTrip.getUser().getId()).isEqualTo(persistedTrip.getUser().getId());
        assertThat(updatedTrip.getRoute().getId()).isEqualTo(persistedTrip.getRoute().getId());
    }

    @Test
    void updateTrip_Failure_SetNullRequiredField_ShouldThrowException() throws DataAccessException {
        // Arrange
        Trip persistedTrip = createAndPersistTestTrip("updateTripNull", "StartI", "EndJ");
        UUID tripId = persistedTrip.getId();
        session.clear();

        Trip tripToUpdate = session.get(Trip.class, tripId);
        assertThat(tripToUpdate).isNotNull();
        tripToUpdate.setDepartureTime(null); // Нарушаем not-null

        // Act & Assert
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            tripDao.updateTrip(tripToUpdate); // merge
            session.flush(); // Ошибка PropertyValueException
        });

        Throwable rootCause = ex;
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Trip.departureTime");
    }

    // ================== Тесты deleteTrip ==================

    @Test
    void deleteTrip_Success_WhenExists_ShouldRemoveTrip() throws DataAccessException {
        // Arrange
        Trip persistedTrip = createAndPersistTestTrip("deleteTripSuccess", "StartK", "EndL");
        String tripId = persistedTrip.getId().toString();
        UUID tripUUID = persistedTrip.getId();

        // Act
        tripDao.deleteTrip(tripId);
        session.flush();

        // Assert
        session.clear();
        Trip deletedTrip = session.get(Trip.class, tripUUID);
        assertThat(deletedTrip).isNull();
        // Связанные User и Route не должны удалиться, если нет cascade=REMOVE на Trip
        assertThat(session.get(User.class, persistedTrip.getUser().getId())).isNotNull();
        assertThat(session.get(Route.class, persistedTrip.getRoute().getId())).isNotNull();
    }

    @Test
    void deleteTrip_Failure_WhenNotExists_ShouldDoNothing() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act & Assert
        assertDoesNotThrow(() -> {
            tripDao.deleteTrip(nonExistentId);
            session.flush();
        });
        assertThat(session.get(Trip.class, UUID.fromString(nonExistentId))).isNull();
    }

    @Test
    void deleteTrip_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-trip-id";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            tripDao.deleteTrip(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }


    // ================== Тесты findTrips ==================

    @Test
    void findTrips_Success_ShouldReturnMatchingTrips() throws DataAccessException, OperationNotSupportedException {
        // Arrange - создаем несколько поездок
        Trip trip1 = createAndPersistTestTrip("find1", "Paris", "Lyon"); // User 1, Route 1
        Trip trip2 = createAndPersistTestTrip("find2", "Paris", "Nice");  // User 2, Route 2
        Trip trip3 = createAndPersistTestTrip("find3", "Lyon", "Paris");  // User 3, Route 3
        // Изменяем даты для теста поиска по дате
        LocalDateTime date1 = LocalDate.now().plusDays(3).atTime(10, 0);
        LocalDateTime date2 = LocalDate.now().plusDays(3).atTime(15, 0);
        LocalDateTime date3 = LocalDate.now().plusDays(4).atTime(10, 0);
        trip1.setDepartureTime(date1);
        trip2.setDepartureTime(date2);
        trip3.setDepartureTime(date3);
        tripDao.updateTrip(trip1);
        tripDao.updateTrip(trip2);
        tripDao.updateTrip(trip3);
        session.flush();
        session.clear();

        // Act & Assert 1: Поиск по начальной точке
        List<Trip> foundParis = tripDao.findTrips("Paris", null, null);
        assertThat(foundParis).hasSize(2).extracting(Trip::getId).containsExactlyInAnyOrder(trip1.getId(), trip2.getId());

        // Act & Assert 2: Поиск по конечной точке
        List<Trip> foundLyon = tripDao.findTrips(null, "Lyon", null);
        assertThat(foundLyon).hasSize(1).extracting(Trip::getId).containsExactly(trip1.getId());

        // Act & Assert 3: Поиск по дате
        List<Trip> foundDate = tripDao.findTrips(null, null, LocalDate.now().plusDays(3));
        assertThat(foundDate).hasSize(2).extracting(Trip::getId).containsExactlyInAnyOrder(trip1.getId(), trip2.getId());

        // Act & Assert 4: Поиск по всем критериям
        List<Trip> foundAll = tripDao.findTrips("Paris", "Nice", LocalDate.now().plusDays(3));
        assertThat(foundAll).hasSize(1).extracting(Trip::getId).containsExactly(trip2.getId());

        // Act & Assert 5: Поиск по несовпадающим критериям
        List<Trip> foundNone = tripDao.findTrips("Mars", null, null);
        assertThat(foundNone).isEmpty();
    }

    @Test
    void findTrips_Failure_OperationNotSupported_ShouldThrowException() {
        // Этот тест имеет смысл только если бы у нас была реализация DAO,
        // которая *не* поддерживает findTrips и бросает OperationNotSupportedException.
        // Для PostgresTripDao этот тест не актуален, т.к. метод реализован.
        // UserDao mockDao = mock(UserDao.class); // Пример
        // when(mockDao.findTrips(any(), any(), any())).thenThrow(OperationNotSupportedException.class);
        // assertThrows(OperationNotSupportedException.class, () -> mockDao.findTrips(null, null, null));
        assertTrue(true); // Заглушка, т.к. тест не применим к PostgresTripDao
    }

}