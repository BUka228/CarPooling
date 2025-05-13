package inheritance.singletable.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("MOTORCYCLE") // Значение для колонки-дискриминатора
public class MotorcycleSingleTable extends VehicleSingleTable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "has_sidecar")
    private Boolean hasSidecar;

    @Column(name = "engine_displacement_cc")
    private Integer engineDisplacementCC;

    public MotorcycleSingleTable(String manufacturer, int modelYear, Boolean hasSidecar, Integer engineDisplacementCC) {
        super(manufacturer, modelYear);
        this.hasSidecar = hasSidecar;
        this.engineDisplacementCC = engineDisplacementCC;
    }
}