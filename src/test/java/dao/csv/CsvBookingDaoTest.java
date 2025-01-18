package dao.csv;

import com.carpooling.dao.csv.CsvBookingDao;
import com.carpooling.entities.record.BookingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvBookingDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private CsvBookingDao bookingDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        tempFile = tempDir.resolve("test-ratings.csv").toFile();
        bookingDao = new CsvBookingDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateBooking_Success() {
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("AB123456");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-1");
        bookingRecord.setUserId("user-1");

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
    void testCreateBooking_Fail() {
        // Создаем временный файл и делаем его недоступным для записи
        File file = tempDir.resolve("test-bookings-fail.csv").toFile();
        try {
            file.createNewFile();
            file.setReadOnly(); // Делаем файл доступным только для чтения
        } catch (Exception e) {
            fail("Не удалось создать или изменить права доступа к файлу.");
        }

        CsvBookingDao failDao = new CsvBookingDao(file.getAbsolutePath());

        // Пытаемся создать бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 1);
        bookingRecord.setStatus("pending");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("CD654321");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-2");
        bookingRecord.setUserId("user-2");

        assertThrows(DataAccessException.class, () -> failDao.createBooking(bookingRecord));

        // Восстанавливаем права доступа к файлу для последующих тестов
        file.setWritable(true);
    }


    @Test
    void testGetBookingById_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 1);
        bookingRecord.setStatus("pending");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("CD654321");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-2");
        bookingRecord.setUserId("user-2");

        String bookingId = bookingDao.createBooking(bookingRecord);

        // Получаем бронирование по ID
        Optional<BookingRecord> foundBooking = bookingDao.getBookingById(bookingId);

        // Проверяем, что бронирование найдено
        assertTrue(foundBooking.isPresent());
        assertEquals(bookingId, foundBooking.get().getId());
        assertEquals("pending", foundBooking.get().getStatus());
    }

    @Test
    void testGetBookingById_NotFound() {
        // Пытаемся получить несуществующее бронирование
        Optional<BookingRecord> foundBooking = bookingDao.getBookingById("non-existent-id");

        // Проверяем, что бронирование не найдено
        assertFalse(foundBooking.isPresent());
    }

    @Test
    void testUpdateBooking_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 3);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("EF987654");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-3");
        bookingRecord.setUserId("user-3");

        String bookingId = bookingDao.createBooking(bookingRecord);

        // Обновляем бронирование
        bookingRecord.setStatus("cancelled");
        bookingDao.updateBooking(bookingRecord);

        // Проверяем, что бронирование обновлено
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

        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(bookingRecord));
    }

    @Test
    void testDeleteBooking_Success() {
        // Создаем тестовое бронирование
        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setSeatCount((byte) 4);
        bookingRecord.setStatus("confirmed");
        bookingRecord.setBookingDate(new Date());
        bookingRecord.setPassportNumber("GH123789");
        bookingRecord.setPassportExpiryDate(new Date());
        bookingRecord.setTripId("trip-4");
        bookingRecord.setUserId("user-4");

        String bookingId = bookingDao.createBooking(bookingRecord);

        // Удаляем бронирование
        bookingDao.deleteBooking(bookingId);

        // Проверяем, что бронирование удалено
        Optional<BookingRecord> deletedBooking = bookingDao.getBookingById(bookingId);
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void testDeleteBooking_NotFound() {
        // Пытаемся удалить несуществующее бронирование
        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking("non-existent-id"));
    }
}