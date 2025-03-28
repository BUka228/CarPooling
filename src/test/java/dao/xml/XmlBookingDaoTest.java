package dao.xml;

import com.carpooling.dao.xml.XmlBookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.enums.BookingStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlBookingDaoTest {

    private XmlBookingDao bookingDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() {
        String testFileName = "test-bookings.xml";
        tempFile = tempDir.resolve(testFileName).toFile();
        bookingDao = new XmlBookingDao(tempFile.getAbsolutePath());
    }

    private Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 2);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPassportNumber("PN123456");
        booking.setPassportExpiryDate(LocalDate.now().plusYears(1));
        return booking;
    }

    @Test
    void createBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<Booking> foundBookingOpt = bookingDao.getBookingById(id); // Проверяем, что бронирование найдено после создания
        assertTrue(foundBookingOpt.isPresent(), "Бронирование должно быть найдено после создания");
        Booking foundBooking = foundBookingOpt.get();

        assertEquals(generatedUUID, foundBooking.getId());
        assertEquals(booking.getNumberOfSeats(), foundBooking.getNumberOfSeats());
        assertEquals(booking.getStatus(), foundBooking.getStatus());
        assertEquals(booking.getPassportNumber(), foundBooking.getPassportNumber());
        // Сравниваем даты, при необходимости (например, обрезаем миллисекунды)
        assertNotNull(foundBooking.getBookingDate());
        assertNotNull(foundBooking.getPassportExpiryDate());
    }

    @Test // Ошибка создания бронирования из-за ошибки с файлом
    void createBooking_DataAccessException_OnFileError() {
        Booking booking = createTestBooking();
        // Имитируем ошибку файла (делаем файл доступным только для чтения *перед* записью)
        assertTrue(tempFile.setWritable(false), "Не удалось установить файл в режим только для чтения для теста");

        // Проверяем, что создание бронирования выбрасывает DataAccessException
        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(booking));

        // Очистка (разрешаем запись для последующих тестов в том же запуске, хотя @TempDir обрабатывает очистку каталога)
        tempFile.setWritable(true);
    }

    @Test // Успешное получение бронирования по ID
    void getBookingById_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        Optional<Booking> foundBooking = bookingDao.getBookingById(id);

        assertTrue(foundBooking.isPresent());
        assertEquals(UUID.fromString(id), foundBooking.get().getId()); // Проверяем ID
        assertEquals(booking.getStatus(), foundBooking.get().getStatus()); // Проверяем некоторые данные
    }

    @Test // Бронирование не найдено по ID
    void getBookingById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<Booking> foundBooking = bookingDao.getBookingById(nonExistentId);

        assertFalse(foundBooking.isPresent());
    }

    @Test // Ошибка получения бронирования по ID из-за ошибки с файлом
    void getBookingById_DataAccessException_OnFileError() throws DataAccessException {
        // Сначала создаем корректную запись
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        assertTrue(tempFile.delete(), "Could not delete temp file for error simulation");
    }


    @Test // Успешное обновление бронирования
    void updateBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);
        UUID bookingUUID = UUID.fromString(id);

        // Получаем созданное бронирование, чтобы убедиться, что у нас есть правильный объект ID
        Booking createdBooking = bookingDao.getBookingById(id).orElseThrow(() -> new AssertionError("Не удалось получить бронирование для теста обновления"));

        // Изменяем сущность
        createdBooking.setStatus(BookingStatus.CANCELLED);
        createdBooking.setNumberOfSeats((byte) 1);

        bookingDao.updateBooking(createdBooking); // Используем объект с правильным UUID

        Optional<Booking> updatedBookingOpt = bookingDao.getBookingById(id);
        assertTrue(updatedBookingOpt.isPresent(), "Бронирование должно существовать после обновления");
        Booking updatedBooking = updatedBookingOpt.get();

        assertEquals(BookingStatus.CANCELLED, updatedBooking.getStatus());
        assertEquals((byte) 1, updatedBooking.getNumberOfSeats());
        assertEquals(bookingUUID, updatedBooking.getId()); // Убеждаемся, что ID не изменился
    }

    @Test
    void updateBooking_NotFound() {
        Booking nonExistentBooking = createTestBooking();
        nonExistentBooking.setId(UUID.randomUUID()); // Give it an ID that wasn't created

        // Проверяем, что обновление несуществующей сущности выбрасывает DataAccessException
        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(nonExistentBooking));
    }

    @Test // Успешное удаление бронирования
    void deleteBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        Optional<Booking> entityBeforeDelete = bookingDao.getBookingById(id);
        assertTrue(entityBeforeDelete.isPresent(), "Бронирование должно существовать перед удалением");

        bookingDao.deleteBooking(id);

        Optional<Booking> entityAfterDelete = bookingDao.getBookingById(id);
        assertFalse(entityAfterDelete.isPresent(), "Бронирование не должно существовать после удаления");
    }

    @Test // Бронирование не найдено при удалении
    void deleteBooking_NotFound() {
        // Предполагается, что deleteBooking должен выбрасывать DataAccessException, если ID не найден, как в примере пользователя
        // Если метод DAO только логирует и не выбрасывает исключение, измените на assertDoesNotThrow
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> bookingDao.deleteBooking(nonExistentId)); // Альтернатива, если DAO не выбрасывает исключение
    }

    @Test // Ошибка удаления бронирования из-за ошибки с файлом
    void deleteBooking_DataAccessException_OnFileError() throws DataAccessException {
        // Создаем элемент
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        // Имитируем ошибку файла (делаем файл доступным только для чтения *перед* записью во время удаления)
        assertTrue(tempFile.setWritable(false), "Не удалось установить файл в режим только для чтения для теста");

        // Проверяем, что удаление выбрасывает DataAccessException
        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking(id));

        // Очистка
        tempFile.setWritable(true);
    }
}
