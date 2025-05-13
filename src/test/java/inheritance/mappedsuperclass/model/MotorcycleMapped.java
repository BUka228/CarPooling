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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "motorcycles_mapped_superclass") // Отдельная таблица для Motorcycle
public class MotorcycleMapped extends VehicleMappedSuperclass {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "has_sidecar")
    private boolean hasSidecar;

    @Column(name = "engine_displacement_cc")
    private int engineDisplacementCC;

    public MotorcycleMapped(String manufacturer, int modelYear, boolean hasSidecar, int engineDisplacementCC) {
        super(manufacturer, modelYear);
        this.hasSidecar = hasSidecar;
        this.engineDisplacementCC = engineDisplacementCC;
    }
}