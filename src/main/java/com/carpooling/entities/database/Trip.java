package com.carpooling.entities.database;

import com.carpooling.adapters.LocalDateTimeAdapter;
import com.carpooling.entities.enums.TripStatus;
import com.opencsv.bean.CsvDate;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@Data
@Entity
@Table(name = "trips")
@XmlRootElement(name = "trip")
@XmlAccessorType(XmlAccessType.FIELD)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "departure_time", nullable = false)
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime departureTime;

    @Column(name = "max_passengers", nullable = false)
    private byte maxPassengers;

    @CreationTimestamp // Автоматически устанавливается при создании
    @Column(name = "creation_date", nullable = false, updatable = false)
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus status;

    @Column(name = "editable", nullable = false)
    private boolean editable = true; // Значение по умолчанию

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Поездка должна иметь создателя
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @XmlTransient
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false) // Поездка должна иметь маршрут
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @XmlTransient
    private Route route;

    // Пересмотреть CascadeType.ALL, возможно нужны более специфичные каскады
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @XmlTransient
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Booking> bookings = new HashSet<>();

    // Пересмотрите CascadeType.ALL
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @XmlTransient
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Rating> ratings = new HashSet<>();
}