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
@Table(name = "ratings")
@XmlRootElement(name = "rating")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @CsvBindByName(column = "id")
    @JsonProperty("id")
    @XmlElement(name = "id")
    private UUID id;

    @Column
    @CsvBindByName(column = "rating")
    @JsonProperty("rating")
    @XmlElement(name = "rating")
    private int rating;

    @Column
    @CsvBindByName(column = "comment")
    @JsonProperty("comment")
    @XmlElement(name = "comment")
    private String comment;

    @Column
    @CsvBindByName(column = "date")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date")
    @XmlElement(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    @XmlTransient
    private Trip trip;
}