package com.carpooling.entities.database;

import com.carpooling.utils.AddressConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Data
@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @CsvBindByName(column = "id")
    @JsonProperty("id")
    @XmlElement(name = "id")
    private UUID id;

    @Column(nullable = false)
    @CsvBindByName(column = "name")
    @JsonProperty("name")
    @XmlElement(name = "name")
    private String name;

    @Column(nullable = false, unique = true)
    @CsvBindByName(column = "email")
    @JsonProperty("email")
    @XmlElement(name = "email")
    private String email;

    @Column(nullable = false)
    @CsvBindByName(column = "password")
    @JsonProperty("password")
    @XmlElement(name = "password")
    private String password;

    @Column
    @CsvBindByName(column = "gender")
    @JsonProperty("gender")
    @XmlElement(name = "gender")
    private String gender;

    @Column
    @CsvBindByName(column = "phone")
    @JsonProperty("phone")
    @XmlElement(name = "phone")
    private String phone;

    @Column
    @CsvBindByName(column = "birthDate")
    @CsvDate("yyyy-MM-dd")
    @JsonProperty("birthDate")
    @XmlElement(name = "birthDate")
    private Date birthDate;

    @Embedded
    @CsvCustomBindByName(column = "address", converter = AddressConverter.class)
    @JsonProperty("address")
    @XmlElement(name = "address")
    private Address address;

    @Column
    @CsvBindByName(column = "preferences")
    @JsonProperty("preferences")
    @XmlElement(name = "preferences")
    private String preferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    @XmlTransient
    private Set<Trip> trips = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    @XmlTransient
    private Set<Booking> bookings = new HashSet<>();
}