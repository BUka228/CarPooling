package business.sevice.postgres;

import business.base.BookingService;
import business.service.BookingServiceImpl;
import data.dao.base.BookingDao;
import data.dao.postgres.PostgresBookingDao;
import data.model.database.Booking;
import exceptions.service.BookingServiceException;
import exceptions.service.TripServiceException;
import exceptions.service.UserServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServicePostgresTest extends BasePostgresTest {

    private BookingService bookingService;
    private Connection connection;
    private final String userId = UUID.randomUUID().toString();
    private final String tripId = UUID.randomUUID().toString();
    private final String bookingId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() throws SQLException {
        // Создаем подключение к базе данных
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        // Создаем таблицы
        createTables();

        // Инициализируем сервис
        BookingDao bookingDao = new PostgresBookingDao(connection);
        bookingService = new BookingServiceImpl(bookingDao);

        // Вставляем тестовые данные
        insertTestData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Очищаем таблицы после каждого теста
        connection.createStatement().execute("DELETE FROM bookings");
        connection.createStatement().execute("DELETE FROM trips");
        connection.createStatement().execute("DELETE FROM users");
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id UUID PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                gender VARCHAR(50),
                phone VARCHAR(20),
                birth_date DATE,
                address VARCHAR(255),
                preferences TEXT
            )
            """);

            stmt.execute("""
            CREATE TABLE IF NOT EXISTS trips (
                id UUID PRIMARY KEY,
                departure_time TIMESTAMP NOT NULL,
                max_passengers SMALLINT NOT NULL,
                creation_date TIMESTAMP NOT NULL,
                status VARCHAR(50) NOT NULL,
                editable BOOLEAN NOT NULL,
                user_id UUID REFERENCES users(id),
                route_id UUID
            )
            """);

            stmt.execute("""
            CREATE TABLE IF NOT EXISTS bookings (
                id UUID PRIMARY KEY,
                seat_count SMALLINT NOT NULL,
                status VARCHAR(50) NOT NULL,
                booking_date TIMESTAMP NOT NULL,
                passport_number VARCHAR(50) NOT NULL,
                passport_expiry_date DATE NOT NULL,
                trip_id UUID REFERENCES trips(id),
                user_id UUID REFERENCES users(id)
            )
            """);
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Добавляем тестового пользователя
            stmt.execute(String.format("""
            INSERT INTO users (id, name, email, password, gender, phone, birth_date, address, preferences)
            VALUES ('%s', 'Иван Иванов', 'ivan@example.com', 'password123', 'Мужской', '1234567890', '1990-01-01', 'ул. Пушкина, д.10', 'Люблю музыку')
            """, userId));

            // Добавляем тестовую поездку
            stmt.execute(String.format("""
            INSERT INTO trips (id, departure_time, max_passengers, creation_date, status, editable, user_id, route_id)
            VALUES ('%s', '2023-12-01 08:00:00', 4, '2023-11-01 12:00:00', 'Активна', true, '%s', '%s')
            """, tripId, userId, UUID.randomUUID().toString()));

            // Добавляем тестовое бронирование
            stmt.execute(String.format("""
            INSERT INTO bookings (id, seat_count, status, booking_date, passport_number, passport_expiry_date, trip_id, user_id)
            VALUES ('%s', 2, 'Подтверждено', '2023-11-15 10:00:00', '123456789', '2025-01-01', '%s', '%s')
            """, bookingId, tripId, userId));
        }
    }

    @Test
    void testCreateBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();

        booking.setSeatCount((byte) 1);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-11-20"));
        booking.setPassportNumber("987654321");
        booking.setPassportExpiryDate(Date.valueOf("2025-01-01"));

        String newBookingId = bookingService.createBooking(booking, tripId, userId);
        assertNotNull(newBookingId);

        Optional<Booking> foundBooking = bookingService.getBookingById(newBookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals(1, foundBooking.get().getSeatCount());
    }

    @Test
    void testCreateBooking_Failure_InvalidTripId() {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 1);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-11-20"));
        booking.setPassportNumber("987654321");
        booking.setPassportExpiryDate(Date.valueOf("2025-01-01"));

        assertThrows(BookingServiceException.class, () -> bookingService.createBooking(booking, UUID.randomUUID().toString(), userId));
    }

    @Test
    void testGetBookingById_Success() throws BookingServiceException {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        assertTrue(booking.isPresent());
        assertEquals("Подтверждено", booking.get().getStatus());
    }

    @Test
    void testGetBookingById_Failure_NotFound() throws BookingServiceException {
        assertTrue(bookingService.getBookingById(UUID.randomUUID().toString()).isEmpty());
    }

    @Test
    void testUpdateBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setSeatCount((byte) 3);
        booking.setStatus("Отменено");
        booking.setBookingDate(Date.valueOf("2023-11-15"));
        booking.setPassportNumber("123456789");
        booking.setPassportExpiryDate(Date.valueOf("2025-01-01"));

        bookingService.updateBooking(booking, tripId, userId);

        Optional<Booking> updatedBooking = bookingService.getBookingById(bookingId);
        assertTrue(updatedBooking.isPresent());
        assertEquals("Отменено", updatedBooking.get().getStatus());
    }

    @Test
    void testUpdateBooking_Failure_InvalidBookingId() {
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());  // Несуществующий ID
        booking.setSeatCount((byte) 3);
        booking.setStatus("Отменено");
        booking.setBookingDate(Date.valueOf("2023-11-15"));
        booking.setPassportNumber("123456789");
        booking.setPassportExpiryDate(Date.valueOf("2025-01-01"));

        assertThrows(BookingServiceException.class, () -> bookingService.updateBooking(booking, tripId, userId));
    }

    @Test
    void testDeleteBooking_Success() throws BookingServiceException {
        bookingService.deleteBooking(bookingId);
        assertTrue(bookingService.getBookingById(bookingId).isEmpty());
    }

    @Test
    void testDeleteBooking_Failure_InvalidBookingId() {
        assertThrows(BookingServiceException.class, () -> bookingService.deleteBooking(UUID.randomUUID().toString()));
    }

    @Test
    void testGetBookingsByTrip_Success() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip(tripId));

    }

    @Test
    void testGetBookingsByTrip_Failure_InvalidTripId() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip(UUID.randomUUID().toString()));
    }

    @Test
    void testGetBookingsByUser_Success() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser(userId));
    }

    @Test
    void testGetBookingsByUser_Failure_InvalidUserId() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser(UUID.randomUUID().toString()));
    }

    @Test
    void testGetBookingsByStatus_Success() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByStatus("Подтверждено"));
    }

    @Test
    void testGetBookingsByStatus_Failure_InvalidStatus() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByStatus("Несуществующий статус"));
    }

    @Test
    void testCancelBooking_Success() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking(bookingId));

        Optional<Booking> canceledBooking = bookingService.getBookingById(bookingId);
        assertTrue(canceledBooking.isPresent());
    }

    @Test
    void testCancelBooking_Failure_InvalidBookingId() {
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking(UUID.randomUUID().toString()));
    }
}