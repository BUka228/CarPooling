package data.dao.postgres;

import data.model.database.Booking;
import data.model.record.BookingRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostgresBookingDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private PostgresBookingDao bookingDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDao = new PostgresBookingDao(connection);
    }

    @Test
    void testCreateBooking_Success() throws Exception {

        String tripId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
 
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("A12345678");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId(tripId);
        bookingRecord.setUserId(userId);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        
        String bookingId = bookingDao.createBooking(bookingRecord);

        
        assertNotNull(bookingId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testCreateBooking_Failure() throws Exception {
        
        BookingRecord bookingRecord = new BookingRecord();
        String tripId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("A12345678");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId(tripId);
        bookingRecord.setUserId(tripId);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);


        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(bookingRecord));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        
        String bookingId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn(bookingId);
        when(resultSet.getByte("seat_count")).thenReturn((byte) 2);
        when(resultSet.getString("status")).thenReturn("CONFIRMED");
        when(resultSet.getTimestamp("booking_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("passport_number")).thenReturn("A12345678");
        when(resultSet.getTimestamp("passport_expiry_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        
        Optional<BookingRecord> booking = bookingDao.getBookingById(bookingId);

        
        assertTrue(booking.isPresent());
        assertEquals(bookingId, booking.get().getId());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetBookingById_NotFound() throws Exception {
        
        String bookingId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        
        Optional<BookingRecord> booking = bookingDao.getBookingById(bookingId);

        
        assertFalse(booking.isPresent());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        
        BookingRecord bookingRecord = new BookingRecord();
        String tripId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        bookingRecord.setId(UUID.randomUUID().toString());
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("A12345678");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId(UUID.randomUUID().toString());
        bookingRecord.setUserId(UUID.randomUUID().toString());

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        
        bookingDao.updateBooking(bookingRecord);

        
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateBooking_NotFound() throws Exception {
        
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setId(UUID.randomUUID().toString());
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("A12345678");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId(UUID.randomUUID().toString());
        bookingRecord.setUserId(UUID.randomUUID().toString());

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(bookingRecord));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBooking_Success() throws Exception {
        
        String bookingId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        
        bookingDao.deleteBooking(bookingId);

        
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBooking_NotFound() throws Exception {
        
        String bookingId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking(bookingId));
        verify(preparedStatement, times(1)).executeUpdate();
    }
}