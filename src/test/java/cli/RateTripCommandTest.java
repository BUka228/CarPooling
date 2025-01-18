package cli;

import com.carpooling.cli.cli.RateTripCommand;
import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.services.base.RatingService;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
class RateTripCommandTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RateTripCommand rateTripCommand;

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateTripCommand = new RateTripCommand(ratingService);
        cmd = new CommandLine(rateTripCommand);
    }

    @Test
    void testRateTripCommand_Success() throws RatingServiceException {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-r", "5",
                "-c", "Отличная поездка!"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        // Мокируем успешное создание оценки
        when(ratingService.createRating(any(Rating.class), eq("trip123")))
                .thenReturn("rating123");

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        originalOut = System.out;
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();
        log.info("Output: {}", output);


        // Assert
        assertEquals(0, exitCode);
        assertEquals("Поездка оценена с ID: rating123", output);
        verify(ratingService, times(1)).createRating(any(Rating.class), eq("trip123"));
    }

    @Test
    void testRateTripCommand_UserNotAuthorized() {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-r", "5",
                "-c", "Отличная поездка!"
        };

        // Убедимся, что текущий пользователь не установлен
        CliContext.setCurrentUserId(null);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.err;
        System.setErr(new PrintStream(outputStream));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        log.info("Output: {}", output);
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(output.contains("Ошибка: Вы не авторизованы."));
    }

    @Test
    void testRateTripCommand_RatingServiceException() throws RatingServiceException {
        // Arrange
        String[] args = {
                "-t", "trip123",
                "-r", "5",
                "-c", "Отличная поездка!"
        };

        // Устанавливаем текущего пользователя
        CliContext.setCurrentUserId("user123");

        outputStream = new ByteArrayOutputStream();
        originalOut = System.err;
        System.setErr(new PrintStream(outputStream));

        // Мокируем исключение при создании оценки
        when(ratingService.createRating(any(Rating.class), eq("trip123")))
                .thenThrow(new RatingServiceException("Ошибка при оценке поездки"));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(output.contains("Ошибка при оценке поездки: Ошибка при оценке поездки"));
    }

    @Test
    void testRateTripCommand_MissingRequiredOptions() {
        // Arrange
        String[] args = {
                // Пропущены обязательные параметры: --tripId и --rating
        };
        outputStream = new ByteArrayOutputStream();
        originalOut = System.err;
        System.setErr(new PrintStream(outputStream));

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Проверяем код выхода
    }
}