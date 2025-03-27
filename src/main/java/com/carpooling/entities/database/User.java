package com.carpooling.entities.database;

import com.carpooling.adapters.LocalDateAdapter;
import com.carpooling.utils.AddressConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
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
    @Column(name = "id", nullable = false)
    @CsvBindByName(column = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    @CsvBindByName(column = "name")
    private String name;

    @NaturalId
    @Column(name = "email", nullable = false, unique = true)
    @CsvBindByName(column = "email")
    private String email;

    @Column(name = "password", nullable = false)
    @ToString.Exclude
    @CsvBindByName(column = "password")
    private String password;

    @Column(name = "gender")
    @CsvBindByName(column = "gender")
    private String gender;

    @Column(name = "phone")
    @CsvBindByName(column = "phone")
    private String phone;

    @Column(name = "birth_date")
    @CsvDate("yyyy-MM-dd")
    @CsvBindByName(column = "birth_date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate birthDate;

    @Embedded
    @CsvCustomBindByName(column = "address", converter = AddressConverter.class)
    @JsonProperty("address")
    @XmlElement(name = "address")
    private Address address;

    @Column(name = "preferences", length = 2000)
    @CsvBindByName(column = "preferences")
    private String preferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @XmlTransient
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Trip> trips = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @XmlTransient
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Booking> bookings = new HashSet<>();
}