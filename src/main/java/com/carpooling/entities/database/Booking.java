package com.carpooling.entities.database;

import com.carpooling.adapters.LocalDateAdapter;
import com.carpooling.adapters.LocalDateTimeAdapter;
import com.carpooling.entities.enums.BookingStatus;
import com.opencsv.bean.CsvDate;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.UUID;


@Data
@Entity
@Table(name = "bookings")
@XmlRootElement(name = "booking")
@XmlAccessorType(XmlAccessType.FIELD)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "number_of_seats", nullable = false)
    private byte numberOfSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "booking_date", nullable = false)
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime bookingDate;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    @CsvDate("yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate passportExpiryDate;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY часто лучше для ManyToOne
    @JoinColumn(name = "trip_id", nullable = false) // Бронирование должно относиться к поездке
    @ToString.Exclude // Избегаем рекурсии в toString()
    @EqualsAndHashCode.Exclude // Избегаем рекурсии в equals/hashCode
    @XmlTransient // Избегаем включения в XML, если не нужно
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY часто лучше для ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Бронирование должно относиться к пользователю
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @XmlTransient
    private User user;
}


