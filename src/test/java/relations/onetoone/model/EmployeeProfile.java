package relations.onetoone.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // Чтобы избежать StackOverflow в toString при двунаправленной связи

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "employee_profiles_oto")
public class EmployeeProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id; // Это будет и PK, и FK

    private String photoUrl;

    @Lob
    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    // @MapsId говорит Hibernate, что это поле ID должно быть заполнено
    // значением ID из сущности, на которую указывает связь @OneToOne 'employee'.
    // Это также делает поле 'id' внешним ключом.
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude // Исключаем из toString для предотвращения StackOverflow
    private Employee employee;


    public EmployeeProfile(String photoUrl, String biography) {
        this.photoUrl = photoUrl;
        this.biography = biography;
    }

    public EmployeeProfile(Employee employee, String photoUrl, String biography) {
        this.employee = employee;
        this.id = employee.getId();
        this.photoUrl = photoUrl;
        this.biography = biography;
    }
}