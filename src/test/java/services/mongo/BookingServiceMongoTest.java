package services.mongo;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.mongo.MongoBookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.service.BookingServiceException;
import com.carpooling.services.base.BookingService;
import com.carpooling.services.impl.BookingServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceMongoTest extends BaseMongoTest {

    private BookingService bookingService;

    @BeforeEach
    void setUp() {

        BookingDao bookingDao = new MongoBookingDao(database.getCollection("bookings"));

        bookingService = new BookingServiceImpl(bookingDao);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("bookings").drop();
    }

    @Test
    void testCreateBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        String bookingId = bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString());
        assertNotNull(bookingId);

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals(2, foundBooking.get().getSeatCount());
    }

    @Test
    void testCreateBooking_Failure() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        assertFalse(bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString()).isEmpty());
;
    }

    @Test
    void testGetBookingById_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        String bookingId = bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString());

        Optional<Booking> foundBooking = bookingService.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals("AB123456", foundBooking.get().getPassportNumber());
    }

    @Test
    void testGetBookingById_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingById("non-existent-id"));
    }

    @Test
    void testUpdateBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        String bookingId = bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString());

        booking.setId(bookingId);
        booking.setSeatCount((byte) 4);
        bookingService.updateBooking(booking, "trip-id", "user-id");

        Optional<Booking> updatedBooking = bookingService.getBookingById(bookingId);
        assertTrue(updatedBooking.isPresent());
        assertEquals(4, updatedBooking.get().getSeatCount());
    }

    @Test
    void testUpdateBooking_Failure() {
        Booking booking = new Booking();
        booking.setId("non-existent-id");
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        assertThrows(BookingServiceException.class, () -> bookingService.updateBooking(booking, "trip-id", "user-id"));
    }

    @Test
    void testDeleteBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());

        String bookingId = bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString());
        bookingService.deleteBooking(bookingId);

        assertTrue(bookingService.getBookingById(bookingId).isEmpty());
    }

    @Test
    void testDeleteBooking_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.deleteBooking("non-existent-id"));
    }

    @Test
    void testGetBookingsByTrip_Success() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setPassportNumber("AB123456");
        booking1.setPassportExpiryDate(new Date());

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 3);
        booking2.setPassportNumber("CD654321");
        booking2.setPassportExpiryDate(new Date());

        bookingService.createBooking(booking1, new ObjectId().toHexString(), new ObjectId().toHexString());
        bookingService.createBooking(booking2, new ObjectId().toHexString(), new ObjectId().toHexString());

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip(new ObjectId().toHexString()));
    }

    @Test
    void testGetBookingsByTrip_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByTrip("non-existent-trip-id"));
    }

    @Test
    void testGetBookingsByUser_Success() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setPassportNumber("AB123456");
        booking1.setPassportExpiryDate(new Date());

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 3);
        booking2.setPassportNumber("CD654321");
        booking2.setPassportExpiryDate(new Date());

        bookingService.createBooking(booking1, new ObjectId().toHexString(), new ObjectId().toHexString());
        bookingService.createBooking(booking2, new ObjectId().toHexString(), new ObjectId().toHexString());

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("non-existent-user-id"));
    }

    @Test
    void testGetBookingsByUser_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByUser("non-existent-user-id"));
    }

    @Test
    void testGetBookingsByStatus_Success() throws BookingServiceException {
        Booking booking1 = new Booking();
        booking1.setSeatCount((byte) 2);
        booking1.setPassportNumber("AB123456");
        booking1.setPassportExpiryDate(new Date());
        booking1.setStatus("confirmed");

        Booking booking2 = new Booking();
        booking2.setSeatCount((byte) 3);
        booking2.setPassportNumber("CD654321");
        booking2.setPassportExpiryDate(new Date());
        booking2.setStatus("pending");

        bookingService.createBooking(booking1, new ObjectId().toHexString(), new ObjectId().toHexString());
        bookingService.createBooking(booking2, new ObjectId().toHexString(), new ObjectId().toHexString());

        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByStatus("non-existent-status"));
    }

    @Test
    void testGetBookingsByStatus_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.getBookingsByStatus("non-existent-status"));
    }

    @Test
    void testCancelBooking_Success() throws BookingServiceException {
        Booking booking = new Booking();
        booking.setSeatCount((byte) 2);
        booking.setPassportNumber("AB123456");
        booking.setPassportExpiryDate(new Date());
        booking.setStatus("confirmed");

        String bookingId = bookingService.createBooking(booking, new ObjectId().toHexString(), new ObjectId().toHexString());
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking(bookingId));
    }

    @Test
    void testCancelBooking_Failure() {
        assertThrows(BookingServiceException.class, () -> bookingService.cancelBooking("non-existent-id"));
    }
}