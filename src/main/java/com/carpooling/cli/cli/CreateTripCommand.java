package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.RouteService;
import com.carpooling.services.base.TripService;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.RouteServiceImpl;
import com.carpooling.services.impl.TripServiceImpl;
import com.carpooling.services.impl.UserServiceImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;


@Command(name = "createTrip", description = "Создание новой поездки")
public class CreateTripCommand implements Runnable {

    @Option(names = {"-d", "--departureDate"}, description = "Дата отправления (гггг-ММ-дд)", required = true)
    private String departureDate;

    @Option(names = {"-t", "--departureTime"}, description = "Время отправления (ЧЧ:мм:сс)", required = true)
    private String departureTime;

    @Option(names = {"-m", "--maxPassengers"}, description = "Максимальное количество пассажиров", required = true)
    private byte maxPassengers;

    @Option(names = {"-s", "--startPoint"}, description = "Начальная точка маршрута", required = true)
    private String startPoint;

    @Option(names = {"-e", "--endPoint"}, description = "Конечная точка маршрута", required = true)
    private String endPoint;

    @Override
    public void run() {
        // Инициализация сервисов
        /*RouteService routeService = new RouteServiceImpl(DaoFactory.getRouteDao(CliContext.getCurrentStorageType()));
        TripService tripService = new TripServiceImpl(
                DaoFactory.getTripDao(CliContext.getCurrentStorageType()),
                routeService
        );
        UserService userService = new UserServiceImpl(DaoFactory.getUserDao(CliContext.getCurrentStorageType()));

        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        try {
            // Получение объекта User по currentUserId
            Optional<User> userOptional = userService.getUserById(currentUserId);
            if (userOptional.isEmpty()) {
                System.err.println("Ошибка: Пользователь с ID " + currentUserId + " не найден.");
                return;
            }
            User user = userOptional.get();

            // Создание объекта Trip
            Trip trip = new Trip();
            trip.setDepartureTime(parseDateTime(departureDate, departureTime)); // Преобразуем строки в Date
            trip.setMaxPassengers(maxPassengers);
            trip.setCreationDate(new Date(System.currentTimeMillis()));
            trip.setStatus("SCHEDULED");
            trip.setEditable(true);

            // Создание объекта Route
            Route route = new Route();
            route.setStartPoint(startPoint);
            route.setEndPoint(endPoint);
            route.setDate(parseDateTime(departureDate, departureTime)); // Преобразуем строки в Date

            // Вызов метода createTrip с объектами Trip, Route и User
            String tripId = tripService.createTrip(trip, route, user);
            CliContext.setCurrentTripId(tripId);
            System.out.println("Поездка создана с ID: " + tripId);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); // Выводим сообщение об ошибке формата даты
        } catch (TripServiceException | UserServiceException e) {
            System.err.println("Ошибка при создании поездки: " + e.getMessage());
        }*/
    }

    // Метод для преобразования строки в Date
    @NotNull
    @Contract("_, _ -> new")
    private Date parseDateTime(String date, String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = date + " " + time;
            return new Date(format.parse(dateTime).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Неверный формат даты или времени: " + date + " " + time);
        }
    }
}