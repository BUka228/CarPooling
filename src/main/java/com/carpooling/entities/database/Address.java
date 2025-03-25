package com.carpooling.entities.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@Data
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    @Column(name = "street")
    @CsvBindByName(column = "street")
    @JsonProperty("street")
    @XmlElement(name = "street")
    private String street;

    @Column(name = "zipcode")
    @CsvBindByName(column = "zipcode")
    @JsonProperty("zipcode")
    @XmlElement(name = "zipcode")
    private String zipcode;

    @Column(name = "city")
    @CsvBindByName(column = "city")
    @JsonProperty("city")
    @XmlElement(name = "city")
    private String city;
}