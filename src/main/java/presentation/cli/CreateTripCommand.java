package presentation.cli;

import business.base.TripService;
import business.service.TripServiceImpl;
import data.model.database.Route;
import data.model.database.Trip;
import exceptions.service.TripServiceException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import presentation.context.CliContext;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Command(name = "createTrip", description = "Создание новой поездки")
public class CreateTripCommand implements Runnable {

    private final TripService tripService;

    public CreateTripCommand() {
        this(new TripServiceImpl());
    }

    public CreateTripCommand(TripService tripService) {
        this.tripService = tripService;
    }

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
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        try {
            Trip trip = new Trip();
            trip.setDepartureTime(parseDateTime(departureDate, departureTime)); // Преобразуем строки в Date
            trip.setMaxPassengers(maxPassengers);
            trip.setCreationDate(new Date(System.currentTimeMillis()));
            trip.setStatus("SCHEDULED");
            trip.setEditable(true);

            Route route = new Route();
            route.setStartPoint(startPoint);
            route.setEndPoint(endPoint);
            route.setDate(parseDateTime(departureDate, departureTime)); // Преобразуем строки в Date

            String tripId = tripService.createTrip(trip, route, currentUserId);
            CliContext.setCurrentTripId(tripId);
            System.out.println("Поездка создана с ID: " + tripId);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); // Выводим сообщение об ошибке
        } catch (TripServiceException e) {
            System.err.println("Ошибка при создании поездки: " + e.getMessage());
        }
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