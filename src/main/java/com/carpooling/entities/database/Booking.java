package com.carpooling.entities.database;

import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;

import javax.xml.bind.annotation.*;
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
    @CsvBindByName(column = "id")
    @JsonProperty("id")
    @XmlElement(name = "id")
    private UUID id;

    @Column
    @CsvBindByName(column = "numberOfSeats")
    @JsonProperty("numberOfSeats")
    @XmlElement(name = "numberOfSeats")
    private byte numberOfSeats;

    @Column
    @CsvBindByName(column = "status")
    @JsonProperty("status")
    @XmlElement(name = "status")
    private String status;

    @Column
    @CsvBindByName(column = "bookingDate")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonProperty("bookingDate")
    @XmlElement(name = "bookingDate")
    private Date bookingDate;

    @Column
    @CsvBindByName(column = "passportNumber")
    @JsonProperty("passportNumber")
    @XmlElement(name = "passportNumber")
    private String passportNumber;

    @Column
    @CsvBindByName(column = "passportExpiryDate")
    @CsvDate("yyyy-MM-dd")
    @JsonProperty("passportExpiryDate")
    @XmlElement(name = "passportExpiryDate")
    private Date passportExpiryDate;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    @XmlTransient
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @XmlTransient
    private User user;
}

