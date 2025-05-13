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
@Table(name = "managers_oto_fk")
public class Manager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Владеющая сторона связи
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "office_id", referencedColumnName = "id", unique = true) // Внешний ключ + UNIQUE
    @ToString.Exclude
    private Office office;

    public Manager(String name) {
        this.name = name;
    }

    // Хелпер для двунаправленной связи (если Office.manager используется)
    public void setOffice(Office office) {
        if (this.office != null) {
            this.office.setManager(null); // Убираем старую связь со стороны Office
        }
        this.office = office;
        if (office != null) {
            office.setManager(this); // Устанавливаем новую связь со стороны Office
        }
    }
}