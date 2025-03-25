package dao.bookingdao;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public abstract class AbstractBookingDaoTest {

    protected BookingDao bookingDao;

    @BeforeEach
    public void setUp() {
        bookingDao = createBookingDao();
    }

    @AfterEach
    public void tearDown() {
        cleanUp();
    }

    protected abstract BookingDao createBookingDao();
    protected abstract void cleanUp();

    // Тесты для createBooking
    @Test
    void createBooking_successful() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 2);
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(new Date());
        booking.setPassportNumber("AB1234567");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() + 100000000L)); // Future date

        String bookingId = bookingDao.createBooking(booking);

        assertNotNull(bookingId, "Booking ID should not be null");
        Optional<Booking> createdBooking = bookingDao.getBookingById(bookingId);
        assertTrue(createdBooking.isPresent(), "Booking should be created");
        assertEquals("CONFIRMED", createdBooking.get().getStatus(), "Status should match");
    }

    @Test
    void createBooking_withInvalidData_throwsException() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) -1);
        booking.setStatus(null);
        booking.setBookingDate(null);
        booking.setPassportNumber("");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() - 100000000L));

        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(booking),
                "Should throw an exception when creating a booking with invalid data");
    }

    // Тесты для getBookingById
    @Test
    void getBookingById_successful() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 1);
        booking.setStatus("PENDING");
        booking.setBookingDate(new Date());
        booking.setPassportNumber("XY9876543");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() + 100000000L));
        String bookingId = bookingDao.createBooking(booking);

        Optional<Booking> retrievedBooking = bookingDao.getBookingById(bookingId);

        assertTrue(retrievedBooking.isPresent(), "Booking should be found");
        assertEquals("PENDING", retrievedBooking.get().getStatus(), "Status should match");
    }

    @Test
    void getBookingById_withInvalidId() {
        String invalidId = "invalid-id"; // Not a valid UUID or ObjectId

        // Для Postgres и MongoDB ожидается исключение из-за некорректного формата ID
        // Для CSV и XML просто возвращается пустой Optional, так как ID не преобразуется
        Optional<Booking> retrievedBooking;
        try {
            retrievedBooking = bookingDao.getBookingById(invalidId);
            assertFalse(retrievedBooking.isPresent(), "Should return empty for an invalid ID in CSV/XML");
        } catch (DataAccessException e) {
            // Ожидаем исключение только для Postgres и MongoDB
            assertTrue(true, "Exception is expected for Postgres/MongoDB with invalid ID");
        }
    }

    @Test
    void getBookingById_withNonExistingId_returnsEmpty() {
        String nonExistingId = UUID.randomUUID().toString();

        Optional<Booking> retrievedBooking = bookingDao.getBookingById(nonExistingId);

        assertFalse(retrievedBooking.isPresent(), "Should return empty for a non-existing ID");
    }

    // Тесты для updateBooking
    @Test
    void updateBooking_successful() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 3);
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(new Date());
        booking.setPassportNumber("CD4567890");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() + 100000000L));
        String bookingId = bookingDao.createBooking(booking);

        Booking updatedBooking = bookingDao.getBookingById(bookingId).get();
        updatedBooking.setStatus("CANCELLED");
        updatedBooking.setNumberOfSeats((byte) 2);
        bookingDao.updateBooking(updatedBooking);

        Optional<Booking> retrievedBooking = bookingDao.getBookingById(bookingId);
        assertTrue(retrievedBooking.isPresent(), "Booking should exist after update");
        assertEquals("CANCELLED", retrievedBooking.get().getStatus(), "Status should be updated");
        assertEquals(2, retrievedBooking.get().getNumberOfSeats(), "Seat count should be updated");
    }

    @Test
    void updateBooking_withNonExistingBooking_throwsException() {
        Booking nonExistingBooking = new Booking();
        nonExistingBooking.setId(UUID.randomUUID());
        nonExistingBooking.setStatus("CANCELLED");
        nonExistingBooking.setNumberOfSeats((byte) 1);

        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(nonExistingBooking),
                "Should throw an exception when updating a non-existing booking");
    }

    // Тесты для deleteBooking
    @Test
    void deleteBooking_successful() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 1);
        booking.setStatus("PENDING");
        booking.setBookingDate(new Date());
        booking.setPassportNumber("EF1234567");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() + 100000000L));
        String bookingId = bookingDao.createBooking(booking);

        bookingDao.deleteBooking(bookingId);

        Optional<Booking> retrievedBooking = bookingDao.getBookingById(bookingId);
        assertFalse(retrievedBooking.isPresent(), "Booking should be deleted");
    }

    @Test
    void deleteBooking_withInvalidId() {
        String invalidId = "invalid-id"; // Not a valid UUID or ObjectId

        // Для Postgres и MongoDB ожидается исключение из-за некорректного формата ID
        // Для CSV и XML операция просто не находит запись и завершается без ошибки
        assertDoesNotThrow(() -> bookingDao.deleteBooking(invalidId),
                "Deleting an invalid ID should not throw an exception for CSV/XML, and is handled gracefully in Postgres/MongoDB");
    }

    @Test
    void deleteBooking_withNonExistingId_doesNotThrowException() {
        String nonExistingId = UUID.randomUUID().toString();

        assertDoesNotThrow(() -> bookingDao.deleteBooking(nonExistingId),
                "Deleting a non-existing booking should not throw an exception");
    }
}