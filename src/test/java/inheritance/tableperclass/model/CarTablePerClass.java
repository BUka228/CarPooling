package inheritance.tableperclass.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
// Для TABLE_PER_CLASS не нужна @AttributeOverrides, так как поля суперкласса дублируются
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
@Table(name = "cars_tpc") // Отдельная таблица для Car
public class CarTablePerClass extends VehicleTablePerClass {

    @Serial
    private static final long serialVersionUID = 1L;

    // Поля id, manufacturer, modelYear будут также существовать в этой таблице

    @Column(name = "number_of_doors")
    private int numberOfDoors;

    @Column(name = "has_air_conditioning")
    private boolean hasAirConditioning;

    public CarTablePerClass(String manufacturer, int modelYear, int numberOfDoors, boolean hasAirConditioning) {
        super(manufacturer, modelYear);
        this.numberOfDoors = numberOfDoors;
        this.hasAirConditioning = hasAirConditioning;
    }
}