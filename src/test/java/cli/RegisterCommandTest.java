package cli;

import com.carpooling.cli.cli.RegisterCommand;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@Slf4j
class RegisterCommandTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegisterCommand registerCommand;

    private CommandLine cmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerCommand = new RegisterCommand(userService); // Передаем мок в конструктор
        cmd = new CommandLine(registerCommand);
    }

    @Test
    void testRegisterCommand_Success() throws UserServiceException {
        // Arrange
        String[] args = {
                "-n", "John Doe",
                "-e", "john.doe@example.com",
                "-p", "password123",
                "-g", "Male",
                "-ph", "1234567890",
                "-b", "1990-01-01",
                "-a", "123 Main St",
                "-pr", "Some preferences"
        };

        User expectedUser = new User(
                "",
                "John Doe",
                "john.doe@example.com",
                "password123",
                "Male",
                "1234567890",
                Date.valueOf("1990-01-01"),
                "123 Main St",
                "Some preferences"
        );

        when(userService.registerUser(expectedUser)).thenReturn("12345");

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(0, exitCode);
        verify(userService, times(1)).registerUser(expectedUser);
    }

    @Test
    void testRegisterCommand_MissingRequiredOption() {
        // Arrange
        String[] args = {
                "-n", "John Doe",
                "-e", "john.doe@example.com",
                "-p", "password123",
                "-g", "Male",
                "-ph", "1234567890",
                // Пропущены обязательные параметры: --birthDate и --address
        };

        // Перенаправляем System.out и System.err
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));

        try {
            // Act
            int exitCode = cmd.execute(args);

            // Assert
            assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Проверяем код выхода

            // Получаем вывод из System.out и System.err
            String output = outputStream.toString();
            String error = errorStream.toString();

            log.info("System.out: {}", output);
            log.info("System.err: {}", error);

            // Проверяем, что в System.err содержится сообщение об ошибке
            assertTrue(error.contains("Missing required options"));
        } finally {
            // Восстанавливаем оригинальные потоки
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    void testRegisterCommand_UserServiceException() throws UserServiceException {
        // Arrange
        String[] args = {
                "-n", "John Doe",
                "-e", "john.doe@example.com",
                "-p", "password123",
                "-g", "Male",
                "-ph", "1234567890",
                "-b", "1990-01-01",
                "-a", "123 Main St",
                "-pr", "Some preferences"
        };

        User expectedUser = new User(
                "",
                "John Doe",
                "john.doe@example.com",
                "password123",
                "Male",
                "1234567890",
                Date.valueOf("1990-01-01"),
                "123 Main St",
                "Some preferences"
        );

        when(userService.registerUser(expectedUser)).thenThrow(new UserServiceException("Registration failed"));

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(0, exitCode); // CommandLine handles exceptions and returns 0
        verify(userService, times(1)).registerUser(expectedUser);
    }
}