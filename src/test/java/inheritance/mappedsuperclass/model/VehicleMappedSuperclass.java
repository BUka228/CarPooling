package inheritance.mappedsuperclass.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class VehicleMappedSuperclass implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Для Serializable

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "model_year", nullable = false)
    private int modelYear;

    public VehicleMappedSuperclass(String manufacturer, int modelYear) {
        this.manufacturer = manufacturer;
        this.modelYear = modelYear;
    }
}