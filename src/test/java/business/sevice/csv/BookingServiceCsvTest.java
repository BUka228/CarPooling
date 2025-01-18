package business.sevice.csv;

import business.base.BookingService;
import business.service.BookingServiceImpl;
import data.dao.base.BookingDao;
import data.dao.csv.CsvBookingDao;
import data.model.database.Booking;
import exceptions.service.BookingServiceException;
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

class BookingServiceCsvTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный CSV-файл
        File csvFile = tempDir.resolve("bookings.csv").toFile();

        // Инициализируем DAO и сервис
        BookingDao bookingDao = new CsvBookingDao(csvFile.getAbsolutePath());
        bookingService = new BookingServiceImpl(bookingDao);
    }

    @Test
    void testCreateBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        String bookingId = bookingService.createBooking(booking, tripId, userId);
        assertNotNull(bookingId);

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals((byte) 2, foundBooking.get().getSeatCount());
    }

    @Test
    void testCreateBookingFailure() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        assertFalse(bookingService.createBooking(booking, tripId, userId).isEmpty());
    }

    @Test
    void testGetBookingByIdSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        String bookingId = bookingService.createBooking(booking, tripId, userId);

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals((byte) 2, foundBooking.get().getSeatCount());
    }

    @Test
    void testGetBookingByIdFailure() throws BookingServiceException {
        Optional<Booking> booking = bookingService.getBookingById("non-existent-id");
        assertFalse(booking.isPresent());
    }

    @Test
    void testUpdateBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        String bookingId = bookingService.createBooking(booking, tripId, userId);

        booking.setId(bookingId);
        booking.setSeatCount((byte) 3);
        bookingService.updateBooking(booking, tripId, userId);

        Optional<Booking> updatedBooking = bookingService.getBookingById(bookingId);
        assertTrue(updatedBooking.isPresent());
        assertEquals((byte) 3, updatedBooking.get().getSeatCount());
    }

    @Test
    void testUpdateBookingFailure() {
        Booking booking = new Booking();
        booking.setId("non-existent-id");
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        assertThrows(BookingServiceException.class, () -> bookingService.updateBooking(booking, tripId, userId));
    }

    @Test
    void testDeleteBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        String bookingId = bookingService.createBooking(booking, tripId, userId);
        bookingService.deleteBooking(bookingId);

        Optional<Booking> deletedBooking = bookingService.getBookingById(bookingId);
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void testDeleteBookingFailure() {
        assertThrows(BookingServiceException.class, () -> bookingService.deleteBooking("non-existent-id"));
    }

    @Test
    void testGetBookingsByTripSuccess() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setStatus("Подтверждено");
        booking1.setBookingDate(Date.valueOf("2023-12-01"));
        booking1.setPassportNumber("1234567890");
        booking1.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 1);
        booking2.setStatus("Подтверждено");
        booking2.setBookingDate(Date.valueOf("2023-12-02"));
        booking2.setPassportNumber("0987654321");
        booking2.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        bookingService.createBooking(booking1, tripId, userId);
        bookingService.createBooking(booking2, tripId, userId);

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip("trip-123"));
    }

    @Test
    void testGetBookingsByTripFailure() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip("non-existent-trip-id"));
    }

    @Test
    void testGetBookingsByUserSuccess() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setStatus("Подтверждено");
        booking1.setBookingDate(Date.valueOf("2023-12-01"));
        booking1.setPassportNumber("1234567890");
        booking1.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 1);
        booking2.setStatus("Подтверждено");
        booking2.setBookingDate(Date.valueOf("2023-12-02"));
        booking2.setPassportNumber("0987654321");
        booking2.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        bookingService.createBooking(booking1, tripId, userId);
        bookingService.createBooking(booking2, tripId, userId);

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("user-123"));
    }

    @Test
    void testGetBookingsByUserFailure() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("non-existent-user-id"));
    }

    @Test
    void testCancelBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setStatus("Подтверждено");
        booking.setBookingDate(Date.valueOf("2023-12-01"));
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2030-01-01"));

        String tripId = "trip-123";
        String userId = "user-123";

        String bookingId = bookingService.createBooking(booking, tripId, userId);
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking(bookingId));

        Optional<Booking> canceledBooking = bookingService.getBookingById(bookingId);
        assertTrue(canceledBooking.isPresent());
    }

    @Test
    void testCancelBookingFailure() {
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking("non-existent-id"));
    }
}
