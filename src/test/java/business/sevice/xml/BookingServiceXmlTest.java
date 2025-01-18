package business.sevice.xml;

import business.base.BookingService;
import business.service.BookingServiceImpl;
import data.dao.base.BookingDao;
import data.dao.xml.XmlBookingDao;
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

class BookingServiceXmlTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный XML-файл
        File xmlFile = tempDir.resolve("bookings.xml").toFile();

        // Инициализируем DAO и сервис
        BookingDao bookingDao = new XmlBookingDao(xmlFile.getAbsolutePath());
        bookingService = new BookingServiceImpl(bookingDao);
    }

    @Test
    void testCreateBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        String bookingId = bookingService.createBooking(booking, "trip-id", "user-id");
        assertNotNull(bookingId);

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals((byte) 2, foundBooking.get().getSeatCount());
    }

    @Test
    void testCreateBookingFailure() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        assertFalse(bookingService.createBooking(booking, "trip-id", "user-id").isEmpty());
    }

    @Test
    void testGetBookingByIdSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        String bookingId = bookingService.createBooking(booking, "trip-id", "user-id");

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals("1234567890", foundBooking.get().getPassportNumber());
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
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        String bookingId = bookingService.createBooking(booking, "trip-id", "user-id");

        booking.setId(bookingId);
        booking.setSeatCount((byte) 3);
        bookingService.updateBooking(booking, "trip-id", "user-id");

        Optional<Booking> updatedBooking = bookingService.getBookingById(bookingId);
        assertTrue(updatedBooking.isPresent());
        assertEquals((byte) 3, updatedBooking.get().getSeatCount());
    }

    @Test
    void testUpdateBookingFailure() {
        Booking booking = new Booking();
        booking.setId("non-existent-id");
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        assertThrows(BookingServiceException.class, () -> bookingService.updateBooking(booking, "trip-id", "user-id"));
    }

    @Test
    void testDeleteBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        String bookingId = bookingService.createBooking(booking, "trip-id", "user-id");
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
        booking1.setPassportNumber("1234567890");
        booking1.setPassportExpiryDate(Date.valueOf("2025-12-31"));
        bookingService.createBooking(booking1, "trip-id", "user-id");

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 3);
        booking2.setPassportNumber("0987654321");
        booking2.setPassportExpiryDate(Date.valueOf("2026-12-31"));
        bookingService.createBooking(booking2, "trip-id", "user-id");

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip("trip-id"));
    }

    @Test
    void testGetBookingsByTripFailure() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip("non-existent-trip-id"));
    }

    @Test
    void testGetBookingsByUserSuccess() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setPassportNumber("1234567890");
        booking1.setPassportExpiryDate(Date.valueOf("2025-12-31"));
        bookingService.createBooking(booking1, "trip-id", "user-id");

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 3);
        booking2.setPassportNumber("0987654321");
        booking2.setPassportExpiryDate(Date.valueOf("2026-12-31"));
        bookingService.createBooking(booking2, "trip-id", "user-id");

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("user-id"));
    }

    @Test
    void testGetBookingsByUserFailure() throws BookingServiceException {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("non-existent-user-id"));
    }

    @Test
    void testCancelBookingSuccess() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("1234567890");
        booking.setPassportExpiryDate(Date.valueOf("2025-12-31"));

        String bookingId = bookingService.createBooking(booking, "trip-id", "user-id");
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking(bookingId));
    }

    @Test
    void testCancelBookingFailure() {
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking("non-existent-id"));
    }
}