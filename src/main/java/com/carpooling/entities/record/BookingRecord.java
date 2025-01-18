package com.carpooling.entities.record;

import com.carpooling.entities.database.Booking;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


/**
 * Класс для представления записи бронирования в формате CSV и XML.
 * Используется для сериализации и десериализации данных бронирования.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "booking")
public class BookingRecord {

    @CsvBindByName
    private String id;

    @CsvBindByName
    private byte seatCount;

    @CsvBindByName
    private String status;

    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date bookingDate;

    @CsvBindByName
    private String passportNumber;

    @CsvDate("yyyy-MM-dd") // Указываем формат даты для срока действия паспорта
    @CsvBindByName
    private Date passportExpiryDate;

    @CsvBindByName
    private String tripId;

    @CsvBindByName
    private String userId;

    /**
     * Конструктор для создания объекта BookingRecord из объекта Booking.
     * @param booking Объект Booking, из которого создается BookingRecord.
     * @param tripId ID поездки
     * @param userId ID пользователя
     */
    public BookingRecord(@NotNull Booking booking, String tripId, String userId) {
        this.id = booking.getId();
        this.seatCount = booking.getSeatCount();
        this.status = booking.getStatus();
        this.bookingDate = booking.getBookingDate();
        this.passportNumber = booking.getPassportNumber();
        this.passportExpiryDate = booking.getPassportExpiryDate();
        this.tripId = tripId;
        this.userId = userId;
    }

    /**
     * Преобразует объект BookingRecord в объект Booking.
     * @return Объект Booking.
     */
    public Booking toBooking() {
        return new Booking(id, seatCount, status, bookingDate, passportNumber, passportExpiryDate);
    }
}