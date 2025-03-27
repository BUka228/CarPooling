package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.BookingException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory
import com.carpooling.services.base.BookingService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Command(name = "bookSeat", description = "Бронирование места в поездке")
public class BookSeatCommand implements Runnable {

    @Option(names = {"-t", "--tripId"}, required = true) private String tripId;
    @Option(names = {"-s", "--seatCount"}, required = true) private byte seatCount;
    @Option(names = {"-p", "--passportNumber"}) private String passportNumber;
    @Option(names = {"-e", "--passportExpiryDate"}) private String passportExpiryDateStr;

    @Override
    public void run() {
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы должны войти в систему (login).");
            return;
        }
        System.out.println("Попытка бронирования мест пользователем ID: " + currentUserId + " на поездку ID: " + tripId);

        try {
            BookingService bookingService = ServiceFactory.getBookingService();

            LocalDate passportExpiryDate = parseExpiryDate(); // Вынесли парсинг
            if (passportExpiryDateStr != null && !passportExpiryDateStr.isBlank() && passportExpiryDate == null) {
                return;
            }

            if (seatCount <= 0) {
                System.err.println("Ошибка: Количество мест должно быть больше нуля.");
                return;
            }

            // Вызов сервиса
            String bookingId = bookingService.createBooking(
                    currentUserId,
                    tripId,
                    seatCount,
                    passportNumber,
                    passportExpiryDate
            );

            System.out.println("Места успешно забронированы!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Используемое хранилище: " + CliContext.getCurrentStorageType());

        } catch (BookingException e) {
            System.err.println("Ошибка бронирования: " + e.getMessage());
        } catch (OperationNotSupportedException e) {
            System.err.println("Ошибка: Операция (" + e.getMessage() + ") не поддерживается текущим хранилищем ("+ CliContext.getCurrentStorageType() +").");
        } catch (DataAccessException e) {
            System.err.println("Ошибка доступа к данным: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper для парсинга даты
    private LocalDate parseExpiryDate() {
        if (passportExpiryDateStr == null || passportExpiryDateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(passportExpiryDateStr);
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка: Неверный формат даты окончания срока паспорта '" + passportExpiryDateStr + "'. Используйте гггг-ММ-дд.");
            return null;
        }
    }
}