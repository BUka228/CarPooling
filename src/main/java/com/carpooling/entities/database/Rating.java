package com.carpooling.entities.database;

import com.carpooling.adapters.LocalDateTimeAdapter;
import com.opencsv.bean.CsvDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;


@Data
@Entity
@Table(name = "ratings")
@XmlRootElement(name = "rating")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "rating", nullable = false)
    private int rating; // Возможно, добавить @Min @Max валидацию позже

    @Column(name = "comment", length = 1000) // Пример ограничения длины
    private String comment;

    @Column(name = "date", nullable = false)
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false) // Оценка должна относиться к поездке
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @XmlTransient
    private Trip trip;
}
