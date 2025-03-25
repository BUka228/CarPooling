package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.BookingServiceException;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.BookingService;
import com.carpooling.services.base.TripService;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.BookingServiceImpl;
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

@Command(name = "bookSeat", description = "Бронирование места в поездке")
public class BookSeatCommand implements Runnable {

    @Option(names = {"-t", "--tripId"}, description = "ID поездки", required = true)
    private String tripId;

    @Option(names = {"-s", "--seatCount"}, description = "Количество мест", required = true)
    private byte seatCount;

    @Option(names = {"-p", "--passportNumber"}, description = "Номер паспорта", required = true)
    private String passportNumber;

    @Option(names = {"-e", "--passportExpiryDate"}, description = "Дата окончания срока паспорта (гггг-ММ-дд)", required = true)
    private String passportExpiryDate;

    @Override
    public void run() {
        /*// Инициализация сервисов
        BookingService bookingService = new BookingServiceImpl(DaoFactory.getBookingDao(CliContext.getCurrentStorageType()));
        TripService tripService = new TripServiceImpl(
                DaoFactory.getTripDao(CliContext.getCurrentStorageType()),
                new RouteServiceImpl(DaoFactory.getRouteDao(CliContext.getCurrentStorageType()))
        );
        UserService userService = new UserServiceImpl(DaoFactory.getUserDao(CliContext.getCurrentStorageType()));

        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        try {
            // Получение объекта Trip по tripId
            Optional<Trip> tripOptional = tripService.getTripById(tripId);
            if (tripOptional.isEmpty()) {
                System.err.println("Ошибка: Поездка с ID " + tripId + " не найдена.");
                return;
            }
            Trip trip = tripOptional.get();

            // Получение объекта User по currentUserId
            Optional<User> userOptional = userService.getUserById(currentUserId);
            if (userOptional.isEmpty()) {
                System.err.println("Ошибка: Пользователь с ID " + currentUserId + " не найден.");
                return;
            }
            User user = userOptional.get();

            // Создание объекта Booking
            Booking booking = new Booking();
            booking.setSeatCount(seatCount);
            booking.setPassportNumber(passportNumber);
            booking.setPassportExpiryDate(parseDate(passportExpiryDate)); // Преобразуем строку в Date

            // Вызов метода createBooking с объектами Trip и User
            String bookingId = bookingService.createBooking(booking, trip, user);
            CliContext.setCurrentBookingId(bookingId);
            System.out.println("Место забронировано с ID: " + bookingId);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); // Выводим сообщение об ошибке формата даты
        } catch (BookingServiceException | TripServiceException | UserServiceException e) {
            System.err.println("Ошибка при бронировании места: " + e.getMessage());
        }*/
    }

    // Метод для преобразования строки в Date
    @NotNull
    @Contract("_ -> new")
    private Date parseDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false); // Запрещаем нестрогий разбор даты
            return new Date(format.parse(date).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Неверный формат даты: " + date);
        }
    }
}