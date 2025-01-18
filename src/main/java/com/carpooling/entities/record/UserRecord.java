package com.carpooling.entities.record;

import com.carpooling.entities.database.User;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Класс для представления записи пользователя в формате CSV и XML.
 * Используется для сериализации и десериализации данных пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "user")
public class UserRecord {
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String password;
    @CsvBindByName
    private String gender;
    @CsvBindByName
    private String phone;
    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date birthDate;
    @CsvBindByName
    private String address;
    @CsvBindByName
    private String preferences;

    /**
     * Конструктор, создающий объект UserRecord из объекта User.
     * @param user Объект User.
     */
    public UserRecord(@NotNull User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.birthDate = user.getBirthDate();
        this.address = user.getAddress();
        this.preferences = user.getPreferences();
    }

    /**
     * Преобразует объект UserRecord в объект User.
     * @return Объект User.
     */
    public User toUser() {
        return new User(id, name, email, password, gender, phone, birthDate, address, preferences);
    }
}
