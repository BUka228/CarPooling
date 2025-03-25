package cli;

import com.carpooling.cli.cli.CreateTripCommand;
import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.services.base.TripService;
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

class CreateTripCommandTest {

    /*@Mock
    private TripService tripService;

    @InjectMocks
    private CreateTripCommand createTripCommand;

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTripCommand = new CreateTripCommand(tripService);
        cmd = new CommandLine(createTripCommand);

        // Перенаправляем System.err для перехвата вывода
        outputStream = new ByteArrayOutputStream();
        originalOut = System.err;
        System.setErr(new PrintStream(outputStream));
    }

    @Test
    void testCreateTripCommand_Success() throws TripServiceException {
        // Arrange
        String[] args = {
                "-d", "2023-10-15",
                "-t", "14:30:00",
                "-m", "4",
                "-s", "Москва",
                "-e", "Санкт-Петербург"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Мокируем создание поездки
        when(tripService.createTrip(any(Trip.class), any(Route.class), eq("user123")))
                .thenReturn("trip123");

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(0, exitCode);
        verify(tripService, times(1)).createTrip(any(Trip.class), any(Route.class), eq("user123"));
    }

    @Test
    void testCreateTripCommand_InvalidDateTimeFormat() {
        // Arrange
        String[] args = {
                "-d", "2023-10-15",
                "-t", "14:30", // Неверный формат времени (должно быть ЧЧ:мм:сс)
                "-m", "4",
                "-s", "Москва",
                "-e", "Санкт-Петербург"
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
        assertTrue(errorOutput.contains("Неверный формат даты или времени: 2023-10-15 14:30"));
    }

    @Test
    void testCreateTripCommand_UserNotAuthorized() {
        // Arrange
        String[] args = {
                "-d", "2023-10-15",
                "-t", "14:30:00",
                "-m", "4",
                "-s", "Москва",
                "-e", "Санкт-Петербург"
        };

        // Убедимся, что текущий пользователь не установлен
        CliContext.setCurrentUserId(null);

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
    }

    @Test
    void testCreateTripCommand_TripServiceException() throws TripServiceException {
        // Arrange
        String[] args = {
                "-d", "2023-10-15",
                "-t", "14:30:00",
                "-m", "4",
                "-s", "Москва",
                "-e", "Санкт-Петербург"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Мокируем исключение при создании поездки
        when(tripService.createTrip(any(Trip.class), any(Route.class), eq("user123")))
                .thenThrow(new TripServiceException("Ошибка при создании поездки"));

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        verify(tripService, times(1)).createTrip(any(Trip.class), any(Route.class), eq("user123"));
    }*/
}