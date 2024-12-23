package com.man;

import cli.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import providers.JdbcDataProvider;
import utils.PostgresConnectionUtil;

import java.sql.SQLException;

@Slf4j
@Getter
@CommandLine.Command(
        name = "CarPoolingCLI",
        mixinStandardHelpOptions = true,
        version = "2.0",
        description = "Интерфейс командной строки для платформы совместных поездок с расширенным взаимодействием с пользователем"
)
public class Main implements Runnable {

    private JdbcDataProvider dataProvider;

    public Main() {
        try {
            this.dataProvider = new JdbcDataProvider(PostgresConnectionUtil.getConnection());
        } catch (SQLException e) {
            log.error("Error initializing database connection: {}", e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main())
                .addSubcommand("search", new SearchCommands())
                .addSubcommand("create-trip", new CreateTripCommand())
                .addSubcommand("manage-trip", new ManageTripCommands())
                .addSubcommand("book", new BookingCommands())
                .addSubcommand("rate", new RateCommands())
                .addSubcommand("history", new HistoryCommands())
                .addSubcommand("profile", new ProfileCommands())
                .addSubcommand("register", new RegisterUserCommand())
                .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Добро пожаловать в обновлённый интерфейс командной строки CarPooling! Используйте --help для получения подробной информации о командах.");
    }
}



