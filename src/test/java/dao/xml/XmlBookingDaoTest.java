package dao.xml;


import com.carpooling.dao.xml.XmlBookingDao;
import com.carpooling.entities.record.BookingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlBookingDaoTest {

    private XmlBookingDao bookingDao;
    @TempDir
    java.nio.file.Path tempDir; // Временная директория для тестов

    private java.io.File tempFile;



    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-bookings.xml").toFile();
        bookingDao = new XmlBookingDao(tempFile.getAbsolutePath());
    }


    @Test
    void testCreateBooking_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("AB123456");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-1");
        bookingRecord.setUserId("user-1");

        // Создаем бронирование
        String bookingId = bookingDao.createBooking(bookingRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(bookingId);
        assertDoesNotThrow(() -> UUID.fromString(bookingId));

        // Проверяем, что бронирование было добавлено
        Optional<BookingRecord> foundBooking = bookingDao.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals("confirmed", foundBooking.get().getStatus());
    }

    @Test
    void testCreateBooking_Failure() {
        // Создаем тестовое бронирование с некорректными данными (например, null)
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setTripId(null); // Некорректные данные
        tempFile.setReadOnly();


        // Проверяем, что создание бронирования выбрасывает исключение
        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(bookingRecord));
    }

    @Test
    void testGetBookingById_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("AB123456");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-1");
        bookingRecord.setUserId("user-1");

        // Создаем бронирование и получаем его ID
        String bookingId = bookingDao.createBooking(bookingRecord);

        // Получаем бронирование по ID
        Optional<BookingRecord> foundBooking = bookingDao.getBookingById(bookingId);
        assertTrue(foundBooking.isPresent());
        assertEquals(bookingId, foundBooking.get().getId());
    }

    @Test
    void testGetBookingById_NotFound() {
        // Пытаемся получить несуществующее бронирование
        Optional<BookingRecord> foundBooking = bookingDao.getBookingById("non-existent-id");
        assertFalse(foundBooking.isPresent());
    }

    @Test
    void testUpdateBooking_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("AB123456");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-1");
        bookingRecord.setUserId("user-1");

        // Создаем бронирование и получаем его ID
        String bookingId = bookingDao.createBooking(bookingRecord);

        // Обновляем бронирование
        bookingRecord.setStatus("cancelled");
        bookingDao.updateBooking(bookingRecord);

        // Проверяем, что бронирование было обновлено
        Optional<BookingRecord> updatedBooking = bookingDao.getBookingById(bookingId);
        assertTrue(updatedBooking.isPresent());
        assertEquals("cancelled", updatedBooking.get().getStatus());
    }

    @Test
    void testUpdateBooking_NotFound() {
        // Пытаемся обновить несуществующее бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setId("non-existent-id");
        bookingRecord.setStatus("confirmed");

        // Проверяем, что обновление выбрасывает исключение
        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(bookingRecord));
    }

    @Test
    void testDeleteBooking_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("AB123456");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-1");
        bookingRecord.setUserId("user-1");

        // Создаем бронирование и получаем его ID
        String bookingId = bookingDao.createBooking(bookingRecord);

        // Удаляем бронирование
        bookingDao.deleteBooking(bookingId);

        // Проверяем, что бронирование было удалено
        Optional<BookingRecord> deletedBooking = bookingDao.getBookingById(bookingId);
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void testDeleteBooking_NotFound() {
        // Пытаемся удалить несуществующее бронирование
        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking("non-existent-id"));
    }
}