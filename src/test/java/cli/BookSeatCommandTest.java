package cli;

import com.carpooling.cli.cli.BookSeatCommand;
import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.service.BookingServiceException;
import com.carpooling.services.base.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;



class BookSeatCommandTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookSeatCommand bookSeatCommand;

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookSeatCommand = new BookSeatCommand(bookingService);
        cmd = new CommandLine(bookSeatCommand);

        // Перенаправляем System.err для перехвата вывода
        outputStream = new ByteArrayOutputStream();
        originalOut = System.err;
        System.setErr(new PrintStream(outputStream));
    }

    @Test
    void testBookSeatCommand_Success() throws BookingServiceException {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-s", "2",
                "-p", "AB123456",
                "-e", "2025-12-31"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Мокируем создание бронирования
        when(bookingService.createBooking(any(Booking.class), eq("trip123"), eq("user123")))
                .thenReturn("booking123");

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.err
        System.setErr(originalOut);

        // Assert
        assertEquals(0, exitCode);
        verify(bookingService, times(1)).createBooking(any(Booking.class), eq("trip123"), eq("user123"));
    }

    @Test
    void testBookSeatCommand_InvalidDateFormat() {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-s", "2",
                "-p", "AB123456",
                "-e", "2025/12/31" // Неверный формат даты
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.err
        System.setErr(originalOut);

        // Получаем вывод из System.err
        String errorOutput = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(errorOutput.contains("Неверный формат даты: 2025/12/31"));
    }

    @Test
    void testBookSeatCommand_UserNotAuthorized() {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-s", "2",
                "-p", "AB123456",
                "-e", "2025-12-31"
        };

        // Убедимся, что текущий пользователь не установлен
        CliContext.setCurrentUserId(null);

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.err
        System.setErr(originalOut);

        // Получаем вывод из System.err
        String errorOutput = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(errorOutput.contains("Ошибка: Вы не авторизованы."));
    }

    @Test
    void testBookSeatCommand_BookingServiceException() throws BookingServiceException {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-s", "2",
                "-p", "AB123456",
                "-e", "2025-12-31"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Мокируем исключение при создании бронирования
        when(bookingService.createBooking(any(Booking.class), eq("trip123"), eq("user123")))
                .thenThrow(new BookingServiceException("Ошибка при бронировании места"));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.err
        System.setErr(originalOut);

        // Получаем вывод из System.err
        String errorOutput = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(errorOutput.contains("Ошибка при бронировании места: Ошибка при бронировании места"));
    }
}