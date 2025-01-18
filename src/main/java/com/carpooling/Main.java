package com.carpooling;

import com.carpooling.cli.cli.*;
import picocli.CommandLine;

import java.util.Scanner;

@CommandLine.Command(name = "carpooling-cli", mixinStandardHelpOptions = true, version = "1.0",
        description = "CLI для приложения совместных поездок на автомобилях.")
public class Main implements Runnable {

    public static void main(String[] args) {

        CommandLine cmd = new CommandLine(new Main());
        cmd.addSubcommand("register", new RegisterCommand());
        cmd.addSubcommand("login", new LoginCommand());
        cmd.addSubcommand("createTrip", new CreateTripCommand());
        cmd.addSubcommand("bookSeat", new BookSeatCommand());
        cmd.addSubcommand("rateTrip", new RateTripCommand());
        cmd.addSubcommand("setStorage", new SetStorageCommand());

        // Если аргументы не переданы, запускаем интерактивный режим
        if (args.length == 0) {
            startInteractiveMode(cmd);
        } else {
            cmd.execute(args);
        }
    }

    @Override
    public void run() {
        System.out.println("Используйте команду --help для просмотра доступных команд.");
    }

    private static void startInteractiveMode(CommandLine cmd) {
        // Устанавливаем кодировку ввода в UTF-8
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в CarPooling CLI! Введите команду или 'exit' для выхода.");

        while (true) {
            System.out.print("> "); // Приглашение для ввода команды
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            // Выполняем команду
            try {
                cmd.execute(input.split(" "));
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }
}



