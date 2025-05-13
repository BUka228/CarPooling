package collections.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AttributeEmbeddable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "attr_name", nullable = false)
    private String name;

    @Column(name = "attr_value")
    private String value;
}