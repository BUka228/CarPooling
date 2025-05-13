package inheritance.mappedsuperclass.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true) // Важно для корректной работы equals/hashCode с полями суперкласса
@ToString(callSuper = true)         // Важно для корректного toString с полями суперкласса
@NoArgsConstructor
@Entity
@Table(name = "cars_mapped_superclass") // Отдельная таблица для Car
public class CarMapped extends VehicleMappedSuperclass {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "number_of_doors")
    private int numberOfDoors;

    @Column(name = "has_air_conditioning")
    private boolean hasAirConditioning;

    public CarMapped(String manufacturer, int modelYear, int numberOfDoors, boolean hasAirConditioning) {
        super(manufacturer, modelYear);
        this.numberOfDoors = numberOfDoors;
        this.hasAirConditioning = hasAirConditioning;
    }
}