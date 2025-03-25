package com.carpooling.entities.database;

import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
    @CsvBindByName(column = "id")
    @JsonProperty("id")
    @XmlElement(name = "id")
    private UUID id;

    @Column
    @CsvBindByName(column = "startingPoint")
    @JsonProperty("startingPoint")
    @XmlElement(name = "startingPoint")
    private String startingPoint;

    @Column
    @CsvBindByName(column = "endingPoint")
    @JsonProperty("endingPoint")
    @XmlElement(name = "endingPoint")
    private String endingPoint;

    @Column
    @CsvBindByName(column = "date")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date")
    @XmlElement(name = "date")
    private Date date;

    @Column
    @CsvBindByName(column = "estimatedDuration")
    @JsonProperty("estimatedDuration")
    @XmlElement(name = "estimatedDuration")
    private short estimatedDuration;
}