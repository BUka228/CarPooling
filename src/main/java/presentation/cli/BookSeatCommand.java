package presentation.cli;

import business.base.BookingService;
import business.service.BookingServiceImpl;
import data.model.database.Booking;
import exceptions.service.BookingServiceException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import presentation.context.CliContext;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Command(name = "bookSeat", description = "Бронирование места в поездке")
public class BookSeatCommand implements Runnable {

    private final BookingService bookingService;

    public BookSeatCommand() {
        this(new BookingServiceImpl());
    }

    public BookSeatCommand(BookingService bookingService) {
        this.bookingService = bookingService;
    }

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
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        try {
            Booking booking = new Booking();
            booking.setSeatCount(seatCount);
            booking.setPassportNumber(passportNumber);
            booking.setPassportExpiryDate(parseDate(passportExpiryDate)); // Преобразуем строку в Date

            String bookingId = bookingService.createBooking(booking, tripId, currentUserId);
            CliContext.setCurrentBookingId(bookingId);
            System.out.println("Место забронировано с ID: " + bookingId);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage()); // Выводим сообщение об ошибке формата даты
        } catch (BookingServiceException e) {
            System.err.println("Ошибка при бронировании места: " + e.getMessage());
        }
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
