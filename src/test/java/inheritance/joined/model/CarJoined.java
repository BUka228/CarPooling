package inheritance.joined.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn; // Важно для JOINED
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "cars_joined_subclass") // Отдельная таблица для специфичных полей Car
// @PrimaryKeyJoinColumn(name = "vehicle_id") // Явно указывает, что PK этой таблицы (vehicle_id)
// также является FK к базовой таблице.
// Если имя PK базового класса 'id', а FK в этой таблице
// должно называться так же ('id'), то эту аннотацию
// можно не указывать, Hibernate сделает это по умолчанию.
// Но для ясности или если имена отличаются, она полезна.
// В нашем случае, если PK в vehicles_joined_base это 'id',
// и мы хотим, чтобы PK в cars_joined_subclass тоже был 'id' и ссылался
// на vehicles_joined_base.id, то @PrimaryKeyJoinColumn не обязателен,
// но Hibernate создаст PK cars_joined_subclass.id и FK на vehicles_joined_base.id.
public class CarJoined extends VehicleJoined {

    @Serial
    private static final long serialVersionUID = 1L;

    // Поля id, manufacturer, modelYear находятся в таблице vehicles_joined_base

    @Column(name = "number_of_doors", nullable = false) // Теперь можно использовать примитивы и NOT NULL
    private int numberOfDoors;

    @Column(name = "has_air_conditioning", nullable = false)
    private boolean hasAirConditioning;

    public CarJoined(String manufacturer, int modelYear, int numberOfDoors, boolean hasAirConditioning) {
        super(manufacturer, modelYear);
        this.numberOfDoors = numberOfDoors;
        this.hasAirConditioning = hasAirConditioning;
    }
}