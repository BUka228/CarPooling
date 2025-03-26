package dao.csv;

import com.carpooling.dao.csv.CsvBookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.enums.BookingStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvBookingDaoTest {

    private CsvBookingDao bookingDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем путь к временному CSV файлу
        String testFileName = "test-bookings.csv";
        Path filePath = tempDir.resolve(testFileName);
        // Важно: AbstractCsvDao ожидает, что файл существует, хотя бы пустой, для чтения заголовков
        Files.createFile(filePath);
        tempFile = filePath.toFile();
        // Инициализируем DAO с путем к временному файлу
        bookingDao = new CsvBookingDao(tempFile.getAbsolutePath());
        // Опционально: Записать заголовок, если DAO сам этого не делает при инициализации/первой записи
        // bookingDao.writeHeaderIfNotExists(); // Предполагаем, что такой метод есть или writeAll его пишет
    }

    // Вспомогательный метод для создания тестовой сущности
    private Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setNumberOfSeats((byte) 2);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPassportNumber("PN123456");
        // Добавляем дату истечения срока действия паспорта, т.к. она есть в сущности и аннотирована
        booking.setPassportExpiryDate(LocalDate.now()); // +1 год

        // Поля Trip и User не аннотированы @CsvBindByName, поэтому их не устанавливаем для CSV
        return booking;
    }

    @Test
    void createBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<Booking> foundBookingOpt = bookingDao.getBookingById(id);
        assertTrue(foundBookingOpt.isPresent(), "Booking should be found after creation");
        Booking foundBooking = foundBookingOpt.get();

        assertEquals(generatedUUID, foundBooking.getId()); // ID генерируется и должен записываться/читаться
        assertEquals(booking.getNumberOfSeats(), foundBooking.getNumberOfSeats());
        assertEquals(booking.getStatus(), foundBooking.getStatus());
        assertEquals(booking.getPassportNumber(), foundBooking.getPassportNumber());
        // Даты могут немного отличаться из-за точности при записи/чтении CSV, нужна осторожная проверка
        assertNotNull(foundBooking.getBookingDate());
        assertNotNull(foundBooking.getPassportExpiryDate());
    }

    @Test
    void createBooking_DataAccessException_OnFileError() {
        Booking booking = createTestBooking();
        // Имитируем ошибку файла (делаем файл только для чтения *перед* записью)
        assertTrue(tempFile.setWritable(false), "Failed to set file to read-only for test");

        // Проверяем, что создание выбрасывает DataAccessException (из-за IOException внутри)
        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(booking));

        // Очистка (разрешаем запись для следующих тестов, хотя @TempDir удалит директорию)
        tempFile.setWritable(true);
    }

    @Test
    void createBooking_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> bookingDao.createBooking(null));
    }

    @Test
    void getBookingById_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        Optional<Booking> foundBooking = bookingDao.getBookingById(id);

        assertTrue(foundBooking.isPresent());
        assertEquals(UUID.fromString(id), foundBooking.get().getId());
        assertEquals(booking.getStatus(), foundBooking.get().getStatus()); // Проверка данных
    }

    @Test
    void getBookingById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<Booking> foundBooking = bookingDao.getBookingById(nonExistentId);

        assertFalse(foundBooking.isPresent());
    }

    @Test
    void getBookingById_DataAccessException_OnFileError() throws DataAccessException, IOException {
        // Сначала создаем валидную запись
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        // Имитируем ошибку чтения (например, удаляем файл после создания)
        assertTrue(Files.deleteIfExists(tempFile.toPath()), "Could not delete temp file for error simulation");
        // Или портим содержимое файла, если DAO его еще может открыть
        // Files.writeString(tempFile.toPath(), "invalid,csv,data");

        // Проверяем, что чтение выбрасывает DataAccessException (из-за IOException внутри)
        assertThrows(DataAccessException.class, () -> bookingDao.getBookingById(id));
    }

    @Test
    void updateBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);
        UUID bookingUUID = UUID.fromString(id);

        // Получаем созданное бронирование, чтобы убедиться, что у нас правильный объект ID
        Booking createdBooking = bookingDao.getBookingById(id).orElseThrow(() -> new AssertionError("Failed to retrieve booking for update test"));

        // Изменяем сущность
        createdBooking.setStatus(BookingStatus.CONFIRMED);
        createdBooking.setNumberOfSeats((byte) 1);

        bookingDao.updateBooking(createdBooking); // Используем объект с правильным UUID

        Optional<Booking> updatedBookingOpt = bookingDao.getBookingById(id);
        assertTrue(updatedBookingOpt.isPresent(), "Booking should exist after update");
        Booking updatedBooking = updatedBookingOpt.get();

        assertEquals(BookingStatus.CONFIRMED, updatedBooking.getStatus());
        assertEquals((byte) 1, updatedBooking.getNumberOfSeats());
        assertEquals(bookingUUID, updatedBooking.getId()); // Убедимся, что ID не изменился
    }

    @Test
    void updateBooking_NotFound() {
        Booking nonExistentBooking = createTestBooking();
        nonExistentBooking.setId(UUID.randomUUID()); // Присваиваем ID, который не был создан

        // Проверяем, что обновление несуществующей сущности выбрасывает DataAccessException
        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(nonExistentBooking));
    }

    @Test
    void updateBooking_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> bookingDao.updateBooking(null));
    }

    @Test
    void deleteBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        Optional<Booking> entityBeforeDelete = bookingDao.getBookingById(id);
        assertTrue(entityBeforeDelete.isPresent(), "Booking should exist before delete");

        // В CSV DAO deleteById не выбрасывает исключение при успехе, а DataAccessException при ошибке
        assertDoesNotThrow(() -> bookingDao.deleteBooking(id));

        Optional<Booking> entityAfterDelete = bookingDao.getBookingById(id);
        assertFalse(entityAfterDelete.isPresent(), "Booking should not exist after delete");
    }

    @Test
    void deleteBooking_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> bookingDao.deleteBooking(nonExistentId));
    }

    @Test
    void deleteBooking_DataAccessException_OnFileError() throws DataAccessException {
        // Создаем элемент
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        // Имитируем ошибку файла (делаем файл только для чтения *перед* записью во время удаления)
        assertTrue(tempFile.setWritable(false), "Failed to set file to read-only for test");

        // Проверяем, что удаление выбрасывает DataAccessException
        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking(id));

        // Очистка
        tempFile.setWritable(true);
    }
}