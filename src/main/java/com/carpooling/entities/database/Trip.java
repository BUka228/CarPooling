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
    @CsvBindByName(column = "id")
    @JsonProperty("id")
    @XmlElement(name = "id")
    private UUID id;

    @Column
    @CsvBindByName(column = "departureTime")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonProperty("departureTime")
    @XmlElement(name = "departureTime")
    private Date departureTime;

    @Column
    @CsvBindByName(column = "maxPassengers")
    @JsonProperty("maxPassengers")
    @XmlElement(name = "maxPassengers")
    private byte maxPassengers;

    @Column
    @CsvBindByName(column = "creationDate")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creationDate")
    @XmlElement(name = "creationDate")
    private Date creationDate;

    @Column
    @CsvBindByName(column = "status")
    @JsonProperty("status")
    @XmlElement(name = "status")
    private String status;

    @Column
    @CsvBindByName(column = "editable")
    @JsonProperty("editable")
    @XmlElement(name = "editable")
    private boolean editable;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @XmlTransient
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @JsonIgnore
    @XmlTransient
    private Route route;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @XmlTransient
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @XmlTransient
    private Set<Rating> ratings = new HashSet<>();
}