package com.carpooling.entities.database;

import com.carpooling.adapters.LocalDateTimeAdapter;
import com.opencsv.bean.CsvDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "routes")
@XmlRootElement(name = "route")
@XmlAccessorType(XmlAccessType.FIELD)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "starting_point", nullable = false)
    private String startingPoint;

    @Column(name = "ending_point", nullable = false)
    private String endingPoint;

    @Column(name = "date") // Может быть null, если маршрут общий?
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime date;

    @Column(name = "estimated_duration") // В минутах?
    private short estimatedDuration;
}