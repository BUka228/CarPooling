package com.carpooling;

import com.carpooling.cli.cli.*; // Импорт всех команд
import com.carpooling.cli.context.CliContext;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory для доступа к сервисам
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(name = "carpooling-cli", mixinStandardHelpOptions = true, version = "1.0",
        description = "CLI для приложения совместных поездок на автомобилях.",
        subcommands = {
                RegisterCommand.class,
                LoginCommand.class,
                CreateTripCommand.class,
                BookSeatCommand.class,
                RateTripCommand.class,
                SetStorageCommand.class,
                ResetPreferencesCommand.class
        })
@Slf4j
public class Main implements Runnable {

    static {
        try {
            log.info("Application starting. Initializing services for storage type: {}", CliContext.getCurrentStorageType());
            // Вызов любого метода ServiceFactory запустит инициализацию синглтонов
            // на основе ТЕКУЩЕГО (загруженного из Preferences) типа хранилища
            ServiceFactory.getUserService();
            log.info("Services initialized successfully.");
        } catch (Throwable e) { // Ловим Throwable на случай серьезных ошибок инициализации
            log.error("CRITICAL ERROR: Failed to initialize services on startup.", e);
            // Вывод в stderr, т.к. логгер может еще не работать
            System.err.println("CRITICAL ERROR: Failed to initialize services on startup.");
            System.err.println("Message: " + e.getMessage());
            // Завершаем работу, если не удалось инициализироваться
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        log.info("Launching CarPooling CLI...");
        // Тип хранилища уже загружен и сервисы инициализированы в статическом блоке
        System.out.println("Текущее хранилище: " + CliContext.getCurrentStorageType());
        System.out.println("Используйте 'setStorage -t <TYPE>' для изменения (требуется перезапуск).");
        System.out.println("-----------------------------------------------------");

        CommandLine cmd = new CommandLine(new Main());

        if (args.length == 0) {
            startInteractiveMode(cmd);
        } else {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        }
    }


    @Override
    public void run() {
        // Вызывается, если не указана подкоманда
        System.out.println("Не указана подкоманда. Используйте '--help' для списка команд.");
        // new CommandLine(this).usage(System.out); // Можно показать usage
    }

    private static void startInteractiveMode(CommandLine cmd) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Включен интерактивный режим. Введите команду или 'exit' для выхода.");
            System.out.println("Используйте '--help' после имени команды для справки по ней.");
            System.out.println("Аргументы с пробелами заключайте в двойные кавычки.");

            while (true) {
                String prompt = "[" + CliContext.getCurrentStorageType();
                String userId = CliContext.getCurrentUserId();
                if (userId != null) {
                    prompt += ", User: " + (userId.length() > 8 ? userId.substring(0, 8) + "..." : userId);
                }
                prompt += "] > ";
                System.out.print(prompt);

                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Выход из программы.");
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }

                String[] inputArgs = parseCommandLine(input);
                if (inputArgs.length > 0) {
                    // Выполняем команду через Picocli
                    // Результат выполнения (exit code) можно использовать, но в интерактивном режиме обычно игнорируется
                    int executionResult = cmd.execute(inputArgs);
                    // Можно добавить логику на основе executionResult, если нужно
                    // System.out.println("Команда завершилась с кодом: " + executionResult);
                } else {
                    System.out.println("Введите команду.");
                }
                System.out.println("---"); // Разделитель
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка в интерактивном режиме: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // parseCommandLine остается без изменений
    public static String[] parseCommandLine(String commandLine) {
        if (commandLine == null || commandLine.isEmpty()) {
            return new String[0];
        }
        List<String> args = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\S+");
        Matcher matcher = pattern.matcher(commandLine);
        while (matcher.find()) {
            args.add(matcher.group(matcher.group(1) != null ? 1 : 0));
        }
        return args.toArray(new String[0]);
    }
}
