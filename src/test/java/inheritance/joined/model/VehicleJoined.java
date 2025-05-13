package inheritance.joined.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vehicles_joined_base") // Таблица для базового класса
@Inheritance(strategy = InheritanceType.JOINED) // Ключевая аннотация
public abstract class VehicleJoined implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY здесь хорошо работает для базовой таблицы
    private Long id;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "model_year", nullable = false)
    private int modelYear;

    public VehicleJoined(String manufacturer, int modelYear) {
        this.manufacturer = manufacturer;
        this.modelYear = modelYear;
    }
}