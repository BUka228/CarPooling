package relations.manytomany.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "students_mtm")
public class Student implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Владеющая сторона связи (здесь Student владеет связью)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // Каскады для удобства
    @JoinTable(
            name = "student_courses_join_table", // Имя промежуточной таблицы
            joinColumns = @JoinColumn(name = "student_id"), // Внешний ключ на Student
            inverseJoinColumns = @JoinColumn(name = "course_id") // Внешний ключ на Course
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Course> courses = new HashSet<>();

    public Student(String name) {
        this.name = name;
    }

    // Методы-хелперы для управления связью
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getStudents().add(this); // Поддерживаем двунаправленность
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getStudents().remove(this); // Поддерживаем двунаправленность
    }
}