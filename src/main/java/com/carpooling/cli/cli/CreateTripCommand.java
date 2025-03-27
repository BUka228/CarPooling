package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.TripException;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory
import com.carpooling.services.base.TripService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Command(name = "createTrip", description = "Создание новой поездки")
public class CreateTripCommand implements Runnable {

    @Option(names = {"-d", "--departureDate"}, required = true) private String departureDateStr;
    @Option(names = {"-t", "--departureTime"}, required = true) private String departureTimeStr;
    @Option(names = {"-m", "--maxPassengers"}, required = true) private byte maxPassengers;
    @Option(names = {"-s", "--startPoint"}, required = true) private String startPoint;
    @Option(names = {"-e", "--endPoint"}, required = true) private String endPoint;

    private static final DateTimeFormatter TIME_FORMATTER_SECONDS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER_NO_SECONDS = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void run() {
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы должны войти в систему (login).");
            return;
        }
        System.out.println("Попытка создания поездки пользователем ID: " + currentUserId);

        try {
            TripService tripService = ServiceFactory.getTripService();

            LocalDateTime departureDateTime = parseDepartureDateTime();
            if (departureDateTime == null) return;

            if (maxPassengers <= 0) {
                System.err.println("Ошибка: Максимальное количество пассажиров должно быть больше нуля.");
                return;
            }

            // Вызов сервиса
            String tripId = tripService.createTrip(
                    currentUserId,
                    startPoint,
                    endPoint,
                    departureDateTime,
                    maxPassengers
            );

            System.out.println("Поездка успешно создана!");
            System.out.println("Trip ID: " + tripId);
            System.out.println("Используемое хранилище: " + CliContext.getCurrentStorageType());

        } catch (TripException e) {
            System.err.println("Ошибка создания поездки: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка доступа к данным: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper для парсинга даты/времени
    private LocalDateTime parseDepartureDateTime() {
        try {
            LocalDate date = LocalDate.parse(departureDateStr);
            LocalTime time;
            try {
                // Сначала пробуем парсить как HH:mm:ss или HH:mm (стандартный парсер)
                time = LocalTime.parse(departureTimeStr);
            } catch (DateTimeParseException e1) {
                System.err.println("Ошибка: Неверный формат времени '" + departureTimeStr + "'. Используйте ЧЧ:мм или ЧЧ:мм:сс.");
                return null;
            }
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка: Неверный формат даты '" + departureDateStr + "'. Используйте гггг-ММ-дд.");
            return null;
        }
    }
}