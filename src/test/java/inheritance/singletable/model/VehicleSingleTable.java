package inheritance.singletable.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vehicles_single_table") // Одна таблица для всей иерархии
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Ключевая аннотация
@DiscriminatorColumn(
        name = "vehicle_type",
        discriminatorType = DiscriminatorType.STRING, // Тип данных дискриминатора
        length = 20 // Длина колонки дискриминатора
)
public abstract class VehicleSingleTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "model_year", nullable = false)
    private int modelYear;

    public VehicleSingleTable(String manufacturer, int modelYear) {
        this.manufacturer = manufacturer;
        this.modelYear = modelYear;
    }
}