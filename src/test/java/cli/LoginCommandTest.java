package cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginCommandTest {

    /*@Mock
    private UserService userService;

    @InjectMocks
    private LoginCommand loginCommand;

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginCommand = new LoginCommand(userService);
        cmd = new CommandLine(loginCommand);

        // Перенаправляем System.out и System.err для перехвата вывода
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testLoginCommand_Success() throws UserServiceException {
        // Arrange
        String[] args = {
                "-e", "user@example.com",
                "-p", "password123"
        };

        User user = new User();
        user.setId("user123");
        user.setName("John Doe");

        // Мокируем успешную авторизацию
        when(userService.authenticateUser("user@example.com", "password123"))
                .thenReturn(Optional.of(user));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode);
        assertEquals("Пользователь авторизован: John Doe", output);
        verify(userService, times(1)).authenticateUser("user@example.com", "password123");
    }

    @Test
    void testLoginCommand_InvalidCredentials() throws UserServiceException {
        // Arrange
        String[] args = {
                "-e", "user@example.com",
                "-p", "wrongpassword"
        };

        // Мокируем неудачную авторизацию
        when(userService.authenticateUser("user@example.com", "wrongpassword"))
                .thenReturn(Optional.empty());

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode);
    }

    @Test
    void testLoginCommand_UserServiceException() throws UserServiceException {
        // Arrange
        String[] args = {
                "-e", "user@example.com",
                "-p", "password123"
        };

        // Мокируем исключение при авторизации
        when(userService.authenticateUser("user@example.com", "password123"))
                .thenThrow(new UserServiceException("Ошибка при авторизации"));

        // Act
        int exitCode = cmd.execute(args);

        // Восстанавливаем оригинальный System.out
        System.setOut(originalOut);

        // Получаем вывод из System.out
        String output = outputStream.toString().trim();

        // Assert
        assertEquals(0, exitCode);
    }

    @Test
    void testLoginCommand_MissingRequiredOptions() {
        // Arrange
        String[] args = {
                // Пропущены обязательные параметры: --email и --password
        };

        // Act
        int exitCode = cmd.execute(args);

        // Assert
        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Проверяем код выхода
    }*/
}