package presentation.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import presentation.context.CliContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SetStorageCommandTest {

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        SetStorageCommand setStorageCommand = new SetStorageCommand();
        cmd = new CommandLine(setStorageCommand);

        // Перенаправляем System.out и System.err для перехвата вывода
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @Test
    void testSetStorageCommand_Success() {
        // Arrange
        String[] args = {
                "-t", "XML"
        };

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальные потоки
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode);
        assertEquals("Тип хранилища установлен: XML", output);
        assertEquals(CliContext.StorageType.XML, CliContext.getCurrentStorageType());
    }

    @Test
    void testSetStorageCommand_InvalidStorageType() {
        // Arrange
        String[] args = {
                "-t", "INVALID_TYPE"
        };

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальные потоки
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Получаем вывод из System.err
        String errorOutput = errorStream.toString().trim();

        // Assert
        assertEquals(0, exitCode); // CommandLine возвращает 0, даже если есть ошибки
        assertTrue(errorOutput.contains("Ошибка: Неверный тип хранилища. Допустимые значения: XML, CSV, MONGO, POSTGRES."));
    }

    @Test
    void testSetStorageCommand_MissingRequiredOptions() {
        // Arrange
        String[] args = {
                // Пропущен обязательный параметр: --type
        };

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Проверяем код выхода
    }
}