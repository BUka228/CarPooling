package relations.onetoonefk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "offices_oto_fk")
public class Office implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Номер офиса должен быть уникальным
    private String roomNumber;

    private int floor;

    // Обратная сторона связи (опционально, для двунаправленности)
    // mappedBy указывает на поле 'office' в классе Manager
    @OneToOne(mappedBy = "office", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Manager manager;

    public Office(String roomNumber, int floor) {
        this.roomNumber = roomNumber;
        this.floor = floor;
    }
}