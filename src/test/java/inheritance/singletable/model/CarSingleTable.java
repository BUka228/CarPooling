package inheritance.singletable.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
// @Table здесь не нужна, так как все в одной таблице
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity // Необходимо, чтобы Hibernate знал этот класс как часть иерархии
@DiscriminatorValue("CAR") // Значение для колонки-дискриминатора
public class CarSingleTable extends VehicleSingleTable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Эти колонки будут NULL для объектов MotorcycleSingleTable в общей таблице
    @Column(name = "number_of_doors") // nullable по умолчанию true
    private Integer numberOfDoors; // Используем Integer, чтобы разрешить NULL

    @Column(name = "has_air_conditioning") // nullable по умолчанию true
    private Boolean hasAirConditioning; // Используем Boolean, чтобы разрешить NULL

    public CarSingleTable(String manufacturer, int modelYear, Integer numberOfDoors, Boolean hasAirConditioning) {
        super(manufacturer, modelYear);
        this.numberOfDoors = numberOfDoors;
        this.hasAirConditioning = hasAirConditioning;
    }
}