package relations.manytomany;

import inheritance.common.GenericDao; // Используем наш GenericDao
import relations.manytomany.model.Course;
import relations.manytomany.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil;
import com.carpooling.hibernate.ThreadLocalSessionContext;
import jakarta.persistence.PersistenceException; // Для отлова ошибок NOT NULL и UNIQUE
import org.hibernate.exception.ConstraintViolationException; // Для проверки ограничений БД
import org.hibernate.PropertyValueException; // Для проверки NOT NULL на уровне Hibernate

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("@ManyToMany Strategy Tests (Student <-> Course)")
class ManyToManyStrategyTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private GenericDao<Student, Long> studentDao;
    private GenericDao<Course, Long> courseDao;

    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }


    @BeforeEach
    void setUp() {
        studentDao = new GenericDao<>(sessionFactory, Student.class);
        courseDao = new GenericDao<>(sessionFactory, Course.class);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            ThreadLocalSessionContext.unbind();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Helper
    private Student createStudent(String name) {
        return new Student(name);
    }
    private Course createCourse(String title) {
        return new Course(title);
    }

    @Nested
    @DisplayName("Student DAO Operations with Courses")
    class StudentTests {

        @Test
        @DisplayName("Save Student with new Courses: Success")
        void saveStudentWithNewCourses_Success() {
            Student student = createStudent("Mike Ross");
            Course course1 = createCourse("Hibernate MTM Basics");
            Course course2 = createCourse("Advanced Java MTM");
            // CascadeType.PERSIST на Student.courses должен сохранить новые курсы

            student.addCourse(course1); // Хелпер управляет двунаправленностью
            student.addCourse(course2);

            studentDao.save(student);
            session.flush();
            Long studentId = student.getId();
            Long course1Id = course1.getId();
            Long course2Id = course2.getId();
            session.clear();

            assertThat(studentId).isNotNull();
            assertThat(course1Id).isNotNull();
            assertThat(course2Id).isNotNull();

            Student foundStudent = studentDao.findById(studentId).orElseThrow();
            assertThat(foundStudent.getName()).isEqualTo("Mike Ross");
            assertThat(foundStudent.getCourses()).isNotNull().hasSize(2);
            assertThat(foundStudent.getCourses()).extracting(Course::getTitle)
                    .containsExactlyInAnyOrder("Hibernate MTM Basics", "Advanced Java MTM");

            // Проверка обратной связи
            Course foundCourse1 = courseDao.findById(course1Id).orElseThrow();
            assertThat(foundCourse1.getStudents()).extracting(Student::getId).contains(studentId);
        }

        @Test
        @DisplayName("Save Student: Failure (Null Name)")
        void saveStudent_NullName_Failure() {
            Student student = createStudent(null);
            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                studentDao.save(student);
                session.flush();
            });
            boolean foundPVE = false;
            while(rootCause != null) {
                if (rootCause instanceof PropertyValueException && rootCause.getMessage().contains("name")) {
                    foundPVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundPVE).isTrue();
        }

        @Test
        @DisplayName("Find Student By Id: Success (With Courses)")
        void findStudentById_WithCourses_Success() {
            Student student = createStudent("Rachel Zane");
            Course c1 = createCourse("Legal Ethics MTM");
            student.addCourse(c1);
            studentDao.save(student); // Сохранит и курс из-за каскада
            session.flush();
            Long studentId = student.getId();
            session.clear();

            Student foundStudent = studentDao.findById(studentId).orElseThrow();
            assertThat(foundStudent.getName()).isEqualTo("Rachel Zane");
            assertThat(foundStudent.getCourses()).isNotNull().hasSize(1);
            assertThat(foundStudent.getCourses().iterator().next().getTitle()).isEqualTo("Legal Ethics MTM");
        }

        @Test
        @DisplayName("Find Student By Id: Failure (Not Found)")
        void findStudentById_NotFound_Failure() {
            Optional<Student> foundOpt = studentDao.findById(-10L);
            assertThat(foundOpt).isNotPresent();
        }

        @Test
        @DisplayName("Update Student: Add/Remove Courses")
        void updateStudent_ModifyCourses_Success() {
            Student student = createStudent("Harvey Specter");
            Course law = createCourse("Corporate Law MTM");
            Course negotiation = createCourse("Negotiation MTM");
            Course finance = createCourse("Finance MTM"); // Новый курс для добавления

            courseDao.save(law); // Сохраняем курсы отдельно
            courseDao.save(negotiation);
            courseDao.save(finance);
            session.flush();

            student.addCourse(law);
            student.addCourse(negotiation);
            studentDao.save(student);
            session.flush();
            Long studentId = student.getId();
            session.clear();

            Student studentToUpdate = studentDao.findById(studentId).orElseThrow();
            session.refresh(studentToUpdate); // Инициализируем курсы
            Course lawCourse = studentToUpdate.getCourses().stream().filter(c -> c.getTitle().equals("Corporate Law MTM")).findFirst().orElseThrow();
            Course financeCourse = courseDao.findById(finance.getId()).orElseThrow(); // Загружаем finance

            studentToUpdate.removeCourse(lawCourse); // Удаляем один курс
            studentToUpdate.addCourse(financeCourse); // Добавляем другой

            studentDao.update(studentToUpdate);
            session.flush();
            session.clear();

            Student updatedStudent = studentDao.findById(studentId).orElseThrow();
            session.refresh(updatedStudent);
            assertThat(updatedStudent.getCourses()).hasSize(2);
            assertThat(updatedStudent.getCourses()).extracting(Course::getTitle)
                    .containsExactlyInAnyOrder("Negotiation MTM", "Finance MTM");
        }

        @Test
        @DisplayName("Delete Student: Success (Enrollments in JoinTable should be removed)")
        void deleteStudent_RemovesEnrollments_Success() {
            Student student = createStudent("Louis Litt");
            Course c1 = createCourse("Financial Analysis MTM");
            Course c2 = createCourse("Opera MTM");
            student.addCourse(c1);
            student.addCourse(c2);

            courseDao.save(c1); // Сохраняем курсы
            courseDao.save(c2);
            studentDao.save(student); // Сохраняем студента, связи создадутся в join table
            session.flush();
            Long studentId = student.getId();
            Long c1Id = c1.getId();
            session.clear();

            studentDao.deleteById(studentId);
            session.flush();
            session.clear();

            assertThat(studentDao.findById(studentId)).isNotPresent();
            // Курсы должны остаться
            assertThat(courseDao.findById(c1Id)).isPresent();
            // Записи в student_courses_join_table для studentId должны быть удалены
            long count = (Long) session.createNativeQuery(
                            "SELECT COUNT(*) FROM student_courses_join_table WHERE student_id = :sid", Long.class)
                    .setParameter("sid", studentId)
                    .uniqueResult();
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("Delete Student: Failure (Not Found)")
        void deleteStudent_NotFound_ShouldDoNothing() {
            assertDoesNotThrow(() -> {
                studentDao.deleteById(-10L);
                session.flush();
            });
        }
    }

    @Nested
    @DisplayName("Course DAO Operations with Students")
    class CourseTests {

        @Test
        @DisplayName("Save Course with new Students: Success")
        void saveCourseWithNewStudents_Success() {
            Course course = createCourse("Advanced MTM Studies");
            Student student1 = createStudent("Donna Paulsen");
            Student student2 = createStudent("Jessica Pearson");
            // CascadeType.PERSIST на Student.courses (владеющая сторона) отвечает за сохранение.
            // Если бы Course был владеющей стороной, то каскад был бы на Course.students.
            // Здесь мы добавляем через Student, который владеет связью.

            student1.addCourse(course);
            student2.addCourse(course);

            // Сначала сохраняем студентов, или курс с каскадом на студентов (если бы курс владел)
            studentDao.save(student1); // Это также сохранит курс из-за CascadeType.PERSIST
            studentDao.save(student2); // Это обновит курс (если он уже есть) или добавит связь
            session.flush();
            Long courseId = course.getId();
            Long student1Id = student1.getId();
            session.clear();

            assertThat(courseId).isNotNull();
            Course foundCourse = courseDao.findById(courseId).orElseThrow();
            assertThat(foundCourse.getTitle()).isEqualTo("Advanced MTM Studies");
            // session.refresh(foundCourse); // Инициализируем студентов
            assertThat(foundCourse.getStudents()).isNotNull().hasSize(2);
            assertThat(foundCourse.getStudents()).extracting(Student::getName)
                    .containsExactlyInAnyOrder("Donna Paulsen", "Jessica Pearson");
        }

        @Test
        @DisplayName("Save Course: Failure (Null Title)")
        void saveCourse_NullTitle_Failure() {
            Course course = createCourse(null);
            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                courseDao.save(course);
                session.flush();
            });
            boolean foundPVE = false;
            while(rootCause != null) {
                if (rootCause instanceof PropertyValueException && rootCause.getMessage().contains("title")) {
                    foundPVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundPVE).isTrue();
        }

        @Test
        @DisplayName("Save Course: Failure (Duplicate Title)")
        void saveCourse_DuplicateTitle_Failure() {
            courseDao.save(createCourse("Unique Course MTM"));
            session.flush();
            // session.clear(); // Не очищаем, чтобы следующее сохранение было в той же транзакции

            Course duplicateCourse = createCourse("Unique Course MTM");
            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                courseDao.save(duplicateCourse);
                session.flush();
            });
            boolean foundCVE = false;
            while(rootCause != null) {
                if (rootCause instanceof ConstraintViolationException cve) {
                    assertThat(cve.getErrorCode() == 23505 || (cve.getSQLState() != null && cve.getSQLState().equals("23505"))).isTrue();
                    foundCVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundCVE).isTrue();
        }

        @Test
        @DisplayName("Find Course By Id: Success (With Students)")
        void findCourseById_WithStudents_Success() {
            Course course = createCourse("Philosophy MTM");
            Student s1 = createStudent("Socrates");
            s1.addCourse(course);
            studentDao.save(s1); // Сохранит и курс
            session.flush();
            Long courseId = course.getId();
            session.clear();

            Course foundCourse = courseDao.findById(courseId).orElseThrow();
            assertThat(foundCourse.getTitle()).isEqualTo("Philosophy MTM");
            session.refresh(foundCourse); // Инициализируем
            assertThat(foundCourse.getStudents()).hasSize(1);
            assertThat(foundCourse.getStudents().iterator().next().getName()).isEqualTo("Socrates");
        }

        @Test
        @DisplayName("Find Course By Id: Failure (Not Found)")
        void findCourseById_NotFound_Failure() {
            Optional<Course> foundOpt = courseDao.findById(-11L);
            assertThat(foundOpt).isNotPresent();
        }

        @Test
        @DisplayName("Delete Course: Success (Enrollments in JoinTable should be removed)")
        void deleteCourse_RemovesEnrollments_Success() {
            Student student1 = createStudent("StudentForDeletedCourse1");
            Student student2 = createStudent("StudentForDeletedCourse2");
            Course courseToDelete = createCourse("CourseToBeDeletedMTM");

            // Записываем студентов на курс
            student1.addCourse(courseToDelete);
            student2.addCourse(courseToDelete);

            courseDao.save(courseToDelete); // Убедимся, что курс существует
            studentDao.save(student1);
            studentDao.save(student2);
            session.flush();

            Long courseId = courseToDelete.getId();
            Long student1Id = student1.getId();
            Long student2Id = student2.getId();
            session.clear();

            // --- Начало исправления ---
            Course courseBeingDeleted = courseDao.findById(courseId).orElseThrow();

            List<Student> studentsEnrolled = new ArrayList<>(courseBeingDeleted.getStudents()); // Создаем копию

            for (Student student : studentsEnrolled) {
                // Загружаем студента в текущую сессию, если он detached (после session.clear())
                Student managedStudent = studentDao.findById(student.getId()).orElseThrow();
                managedStudent.removeCourse(courseBeingDeleted); // Это обновит и студента, и курс (в памяти)
                studentDao.update(managedStudent); // Сохраняем изменения студента (удаление из join table)
            }
            session.flush(); // Применяем удаление связей из join table
            session.clear(); // Очищаем сессию перед удалением самого курса

            // Теперь курс можно безопасно удалить
            courseDao.deleteById(courseId);
            session.flush();
            session.clear();
            // --- Конец исправления ---

            assertThat(courseDao.findById(courseId)).isNotPresent();
            // Студенты должны остаться
            assertThat(studentDao.findById(student1Id)).isPresent();
            assertThat(studentDao.findById(student2Id)).isPresent();

            // У студентов больше не должно быть этого курса
            Student foundStudent1 = studentDao.findById(student1Id).orElseThrow();
            session.refresh(foundStudent1); // Обновляем для загрузки ленивой коллекции
            assertThat(foundStudent1.getCourses()).isEmpty();

            Student foundStudent2 = studentDao.findById(student2Id).orElseThrow();
            session.refresh(foundStudent2);
            assertThat(foundStudent2.getCourses()).isEmpty();

            // Записи в student_courses_join_table для courseId должны быть удалены
            long count = (Long) session.createNativeQuery(
                            "SELECT COUNT(*) FROM student_courses_join_table WHERE course_id = :cid", Long.class)
                    .setParameter("cid", courseId)
                    .uniqueResult();
            assertThat(count).isZero();
        }
    }
}