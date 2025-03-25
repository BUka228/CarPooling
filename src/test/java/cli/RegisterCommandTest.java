package cli;

import static org.mockito.ArgumentMatchers.any;


class RegisterCommandTest {

    /*private RegisterCommand registerCommand;

    @Mock
    private UserService userService;

    @Mock
    private UserDao userDao;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
        System.setOut(new PrintStream(outputStream)); // Перехват вывода в консоль
        System.setErr(new PrintStream(outputStream)); // Перехват ошибок в консоль

        // Явно устанавливаем тип хранилища
        CliContext.setCurrentStorageType(CliContext.StorageType.CSV);

        // Мокируем DaoFactory
        when(DaoFactory.getUserDao(any(CliContext.StorageType.class))).thenReturn(userDao);

        // Создаем экземпляр команды
        registerCommand = new RegisterCommand();

        // Устанавливаем значения через сеттеры
        registerCommand.setName("John Doe");
        registerCommand.setEmail("john.doe@example.com");
        registerCommand.setPassword("password123");
        registerCommand.setGender("Male");
        registerCommand.setPhone("1234567890");
        registerCommand.setBirthDate("1990-01-01");
        registerCommand.setAddress("123 Main St");
        registerCommand.setPreferences("No preferences");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut); // Восстановление стандартного вывода
        System.setErr(originalErr); // Восстановление стандартного вывода ошибок
    }

    @Test
    void testRun_SuccessfulRegistration() throws UserServiceException {
        // Мокируем UserService
        when(userService.registerUser(any(User.class))).thenReturn("user123");

        // Запуск команды
        registerCommand.run();

        // Проверка вывода в консоль
        String output = outputStream.toString();
        assertTrue(output.contains("Пользователь зарегистрирован с ID: user123"));

        // Проверка, что ID пользователя установлен в CliContext
        assertEquals("user123", CliContext.getCurrentUserId());
    }

    @Test
    void testRun_RegistrationFailure() throws UserServiceException {
        // Мокируем UserService для выброса исключения
        when(userService.registerUser(any(User.class))).thenThrow(new UserServiceException("Registration failed"));

        // Запуск команды
        registerCommand.run();

        // Проверка вывода ошибки в консоль
        String output = outputStream.toString();
        assertTrue(output.contains("Ошибка при регистрации пользователя: Registration failed"));

        // Проверка, что ID пользователя не установлен в CliContext
        assertNull(CliContext.getCurrentUserId());
    }

    @Test
    void testRun_InvalidBirthDate() {
        // Устанавливаем неверный формат даты
        registerCommand.setBirthDate("invalid-date");

        // Запуск команды
        registerCommand.run();

        // Проверка вывода ошибки в консоль
        String output = outputStream.toString();
        assertTrue(output.contains("Ошибка при регистрации пользователя"));
    }*/
}
