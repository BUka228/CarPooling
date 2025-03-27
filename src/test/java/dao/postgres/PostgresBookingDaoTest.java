package dao.postgres;

import com.carpooling.dao.base.*; // Импорт всех базовых DAO
import com.carpooling.dao.postgres.PostgresBookingDao;
import com.carpooling.dao.postgres.PostgresRouteDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.*; // Импорт всех сущностей
import com.carpooling.entities.enums.BookingStatus;
import com.carpooling.entities.enums.TripStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException; // Для методов find*
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


class PostgresBookingDaoTest {

    private static SessionFactory sessionFactory;
    // DAO для зависимостей
    private UserDao userDao;
    private RouteDao routeDao;
    private TripDao tripDao;
    // Тестируемый DAO
    private BookingDao bookingDao;

    private Session session;
    private Transaction transaction;

    private User testUser1;
    private User testUser2;
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
    void setUp() throws DataAccessException { // Добавляем throws DataAccessException из-за createTestData
        // Создаем все DAO
        userDao = new PostgresUserDao(sessionFactory);
        routeDao = new PostgresRouteDao(sessionFactory);
        tripDao = new PostgresTripDao(sessionFactory);
        bookingDao = new PostgresBookingDao(sessionFactory);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();

        // Создаем базовые тестовые данные перед каждым тестом
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

    // Хелпер для User
    private User createAndPersistTestUser(String emailSuffix) throws DataAccessException {
        User user = new User();
        user.setName("Book Test User " + emailSuffix);
        user.setEmail("bookuser." + emailSuffix + "@test.com");
        user.setPassword("pass123");
        user.setBirthDate(LocalDate.of(1995, 3, 10));
        userDao.createUser(user);
        session.flush(); // Flush нужен, чтобы user был сохранен для Trip
        return user;
    }

    // Хелпер для Route
    private Route createAndPersistTestRoute(String start, String end) throws DataAccessException {
        Route route = new Route();
        route.setStartingPoint(start);
        route.setEndingPoint(end);
        routeDao.createRoute(route);
        session.flush(); // Flush нужен, чтобы route был сохранен для Trip
        return route;
    }

    // Хелпер для Trip
    private Trip createAndPersistTestTrip(User user, Route route, LocalDateTime departure) throws DataAccessException {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setRoute(route);
        trip.setDepartureTime(departure);
        trip.setMaxPassengers((byte) 3);
        trip.setStatus(TripStatus.PLANNED);
        trip.setEditable(true);
        tripDao.createTrip(trip);
        session.flush(); // Flush нужен, чтобы trip был сохранен для Booking
        return trip;
    }

    // Хелпер для создания и сохранения Booking
    private Booking createAndPersistTestBooking(User user, Trip trip, byte seats) throws DataAccessException {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrip(trip);
        booking.setNumberOfSeats(seats);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now().minusHours(1)); // Время в прошлом
        bookingDao.createBooking(booking);
        session.flush();
        session.clear();
        return booking;
    }

    // Метод для создания тестовых данных в @BeforeEach
    private void createTestData() throws DataAccessException {
        testUser1 = createAndPersistTestUser("1");
        testUser2 = createAndPersistTestUser("2");
        Route route1 = createAndPersistTestRoute("CityX", "CityY");
        Route route2 = createAndPersistTestRoute("CityY", "CityZ");
        testTrip1 = createAndPersistTestTrip(testUser1, route1, LocalDateTime.now().plusDays(1));
        testTrip2 = createAndPersistTestTrip(testUser2, route2, LocalDateTime.now().plusDays(2));
        session.clear(); // Очищаем после подготовки
    }

    // ================== Тесты createBooking ==================

    @Test
    void createBooking_Success_ShouldPersistAndReturnId() throws DataAccessException {
        // Arrange
        Booking booking = new Booking();
        booking.setUser(testUser1); // Используем созданного в @BeforeEach
        booking.setTrip(testTrip2); // На другую поездку
        booking.setNumberOfSeats((byte) 1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPassportNumber("BK123");
        booking.setPassportExpiryDate(LocalDate.now().plusYears(1));

        // Act
        String bookingIdStr = bookingDao.createBooking(booking);
        UUID generatedId = booking.getId();
        session.flush();

        // Assert
        assertThat(generatedId).isNotNull();
        assertThat(bookingIdStr).isEqualTo(generatedId.toString());

        session.clear();
        Booking foundBooking = session.get(Booking.class, generatedId);
        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getUser().getId()).isEqualTo(testUser1.getId());
        assertThat(foundBooking.getTrip().getId()).isEqualTo(testTrip2.getId());
        assertThat(foundBooking.getNumberOfSeats()).isEqualTo((byte) 1);
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void createBooking_Failure_NullUser_ShouldThrowException() {
        // Arrange
        Booking booking = new Booking();
        booking.setUser(null); // User = null (нарушение not-null FK)
        booking.setTrip(testTrip1);
        booking.setNumberOfSeats((byte) 1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now());

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.createBooking(booking);
            session.flush();
        });

        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Booking.user");
    }

    @Test
    void createBooking_Failure_NullTrip_ShouldThrowException() {
        // Arrange
        Booking booking = new Booking();
        booking.setUser(testUser2);
        booking.setTrip(null); // Trip = null (нарушение not-null FK)
        booking.setNumberOfSeats((byte) 1);
        booking.setStatus(BookingStatus.CONFIRMED); // Установим статус
        booking.setBookingDate(LocalDateTime.now());

        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.createBooking(booking);
            session.flush();
        });
        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Booking.trip"); // Теперь проверка должна пройти
    }


    // ================== Тесты getBookingById ==================

    @Test
    void getBookingById_Success_WhenExists_ShouldReturnBookingWithDetails() throws DataAccessException {
        // Arrange
        Booking persistedBooking = createAndPersistTestBooking(testUser1, testTrip1, (byte) 2);
        String bookingId = persistedBooking.getId().toString();

        // Act
        Optional<Booking> foundOpt = bookingDao.getBookingById(bookingId); // DAO использует HQL с JOIN FETCH

        // Assert
        assertThat(foundOpt).isPresent();
        Booking foundBooking = foundOpt.get();
        assertThat(foundBooking.getId()).isEqualTo(persistedBooking.getId());
        assertThat(foundBooking.getNumberOfSeats()).isEqualTo((byte) 2);
        assertThat(foundBooking.getUser()).isNotNull(); // Проверяем загрузку связей
        assertThat(foundBooking.getTrip()).isNotNull();
        assertThat(foundBooking.getUser().getId()).isEqualTo(testUser1.getId());
        assertThat(foundBooking.getTrip().getId()).isEqualTo(testTrip1.getId());
    }

    @Test
    void getBookingById_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act
        Optional<Booking> foundOpt = bookingDao.getBookingById(nonExistentId);
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void getBookingById_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "not-a-booking-uuid";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.getBookingById(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    // ================== Тесты updateBooking ==================

    @Test
    void updateBooking_Success_ShouldUpdateFields() throws DataAccessException {
        // Arrange
        Booking persistedBooking = createAndPersistTestBooking(testUser1, testTrip1, (byte) 1);
        UUID bookingId = persistedBooking.getId();
        session.clear();

        Booking bookingToUpdate = session.get(Booking.class, bookingId);
        assertThat(bookingToUpdate).isNotNull();
        bookingToUpdate.setStatus(BookingStatus.CANCELLED);
        bookingToUpdate.setPassportNumber("UPDATED_PASSPORT");

        // Act
        bookingDao.updateBooking(bookingToUpdate); // merge
        session.flush();
        session.clear();

        // Assert
        Booking updatedBooking = session.get(Booking.class, bookingId);
        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(updatedBooking.getPassportNumber()).isEqualTo("UPDATED_PASSPORT");
        // Убедимся, что другие поля не изменились
        assertThat(updatedBooking.getNumberOfSeats()).isEqualTo((byte) 1);
        assertThat(updatedBooking.getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    void updateBooking_Failure_SetNullRequiredField_ShouldThrowException() throws DataAccessException {
        // Arrange
        Booking persistedBooking = createAndPersistTestBooking(testUser2, testTrip2, (byte) 1);
        UUID bookingId = persistedBooking.getId();
        session.clear();

        Booking bookingToUpdate = session.get(Booking.class, bookingId);
        assertThat(bookingToUpdate).isNotNull();
        bookingToUpdate.setStatus(null); // Нарушаем not-null

        // Act & Assert
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            bookingDao.updateBooking(bookingToUpdate); // merge
            session.flush(); // Ошибка PropertyValueException
        });

        Throwable rootCause = ex;
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("Booking.status");
    }

    // ================== Тесты deleteBooking ==================

    @Test
    void deleteBooking_Success_WhenExists_ShouldRemoveBooking() throws DataAccessException {
        // Arrange
        Booking persistedBooking = createAndPersistTestBooking(testUser2, testTrip1, (byte) 3);
        String bookingId = persistedBooking.getId().toString();
        UUID bookingUUID = persistedBooking.getId();

        // Act
        bookingDao.deleteBooking(bookingId);
        session.flush();

        // Assert
        session.clear();
        Booking deletedBooking = session.get(Booking.class, bookingUUID);
        assertThat(deletedBooking).isNull();
    }

    @Test
    void deleteBooking_Failure_WhenNotExists_ShouldDoNothing() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act & Assert
        assertDoesNotThrow(() -> {
            bookingDao.deleteBooking(nonExistentId);
        });
        // Assert: Проверяем, что ничего не удалилось
        assertThat(session.get(Booking.class, UUID.fromString(nonExistentId))).isNull();
    }

    @Test
    void deleteBooking_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "abc";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.deleteBooking(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }


    // ================== Тесты countBookedSeatsForTrip ==================

    @Test
    void countBookedSeatsForTrip_Success_WhenBookingsExist_ShouldReturnSum() throws DataAccessException, OperationNotSupportedException {
        // Arrange
        createAndPersistTestBooking(testUser1, testTrip1, (byte) 2);
        createAndPersistTestBooking(testUser2, testTrip1, (byte) 1);
        // Еще одно бронирование на другую поездку (не должно учитываться)
        createAndPersistTestBooking(testUser1, testTrip2, (byte) 1);

        // Act
        int count = bookingDao.countBookedSeatsForTrip(testTrip1.getId().toString());

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    void countBookedSeatsForTrip_Success_WhenNoBookings_ShouldReturnZero() throws DataAccessException, OperationNotSupportedException {
        // Arrange: testTrip2 не имеет бронирований

        // Act
        int count = bookingDao.countBookedSeatsForTrip(testTrip2.getId().toString());

        // Assert
        assertThat(count).isZero();
    }

    @Test
    void countBookedSeatsForTrip_Failure_InvalidTripIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-trip";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.countBookedSeatsForTrip(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for trip ID");
    }


    // ================== Тесты findBookingsByUserId ==================

    @Test
    void findBookingsByUserId_Success_WhenBookingsExist_ShouldReturnList() throws DataAccessException, OperationNotSupportedException {
        // Arrange
        Booking booking1 = createAndPersistTestBooking(testUser1, testTrip1, (byte) 1);
        Booking booking2 = createAndPersistTestBooking(testUser1, testTrip2, (byte) 2);
        // Бронирование другого пользователя (не должно попасть в выборку)
        createAndPersistTestBooking(testUser2, testTrip1, (byte) 1);

        // Act
        List<Booking> bookings = bookingDao.findBookingsByUserId(testUser1.getId().toString());

        // Assert
        assertThat(bookings)
                .isNotNull()
                .hasSize(2)
                .extracting(Booking::getId)
                // Запрос сортирует по дате DESC, booking2 создался позже
                .containsExactly(booking2.getId(), booking1.getId());
        // Проверяем, что связанные сущности загружены (из-за JOIN FETCH в HQL)
        assertThat(bookings.get(0).getTrip()).isNotNull();
        assertThat(bookings.get(0).getTrip().getRoute()).isNotNull();
    }

    @Test
    void findBookingsByUserId_Success_WhenNoBookings_ShouldReturnEmptyList() throws DataAccessException, OperationNotSupportedException {
        // Arrange: У testUser2 нет бронирований (после очистки в @BeforeEach и createTestData)

        // Act
        List<Booking> bookings = bookingDao.findBookingsByUserId(testUser2.getId().toString());

        // Assert
        assertThat(bookings).isNotNull().isEmpty();
    }

    @Test
    void findBookingsByUserId_Failure_InvalidUserIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "bad-user-id";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.findBookingsByUserId(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format for user ID");
    }


    // ================== Тесты findBookingByUserAndTrip ==================

    @Test
    void findBookingByUserAndTrip_Success_WhenExists_ShouldReturnBooking() throws DataAccessException, OperationNotSupportedException {
        // Arrange
        Booking persistedBooking = createAndPersistTestBooking(testUser1, testTrip1, (byte) 1);

        // Act
        Optional<Booking> foundOpt = bookingDao.findBookingByUserAndTrip(testUser1.getId().toString(), testTrip1.getId().toString());

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedBooking.getId());
    }

    @Test
    void findBookingByUserAndTrip_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException, OperationNotSupportedException {
        // Arrange: Пользователь 2 не бронировал поездку 1
        // Act
        Optional<Booking> foundOpt = bookingDao.findBookingByUserAndTrip(testUser2.getId().toString(), testTrip1.getId().toString());
        // Assert
        assertThat(foundOpt).isEmpty();
    }

    @Test
    void findBookingByUserAndTrip_Failure_InvalidUserIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "invalid-user";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            bookingDao.findBookingByUserAndTrip(invalidId, testTrip1.getId().toString());
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

}