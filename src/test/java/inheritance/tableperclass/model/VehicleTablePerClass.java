package inheritance.tableperclass.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vehicles_tpc") // Таблица для базового класса Vehicle
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // Ключевая аннотация
// @MappedSuperclass // Эту аннотацию здесь использовать не нужно, если Vehicle - сущность
public class VehicleTablePerClass implements Serializable { // Может быть и не абстрактным

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    // Для TABLE_PER_CLASS важно, чтобы ID были уникальны для всей иерархии.
    // IDENTITY здесь не подойдет, так как каждая таблица будет иметь свой автоинкремент.
    // Используем SEQUENCE или TABLE генератор.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpc_vehicle_seq_generator")
    @SequenceGenerator(
            name = "tpc_vehicle_seq_generator",
            sequenceName = "tpc_vehicle_sequence", // Имя последовательности в БД
            allocationSize = 1 // Сколько ID резервировать за раз
    )
    private Long id;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "model_year", nullable = false)
    private int modelYear;

    public VehicleTablePerClass(String manufacturer, int modelYear) {
        this.manufacturer = manufacturer;
        this.modelYear = modelYear;
    }
}