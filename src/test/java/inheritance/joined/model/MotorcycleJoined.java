package inheritance.joined.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "motorcycles_joined_subclass")
public class MotorcycleJoined extends VehicleJoined {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "has_sidecar", nullable = false)
    private boolean hasSidecar;

    @Column(name = "engine_displacement_cc", nullable = false)
    private int engineDisplacementCC;

    public MotorcycleJoined(String manufacturer, int modelYear, boolean hasSidecar, int engineDisplacementCC) {
        super(manufacturer, modelYear);
        this.hasSidecar = hasSidecar;
        this.engineDisplacementCC = engineDisplacementCC;
    }
}