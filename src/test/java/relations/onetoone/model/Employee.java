package relations.onetoone.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "employees_oto")
public class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private EmployeeProfile profile;

    public Employee(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    // методы-хелперы для синхронизации двунаправленной связи
    public void setProfile(EmployeeProfile profile) {
        if (profile == null) {
            if (this.profile != null) {
                this.profile.setEmployee(null);
            }
        } else {
            profile.setEmployee(this);
        }
        this.profile = profile;
    }
}