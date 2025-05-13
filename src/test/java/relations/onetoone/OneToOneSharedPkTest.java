package relations.onetoone;

import inheritance.common.GenericDao; // Путь к твоему Generic DAO
import relations.onetoone.model.Employee;
import relations.onetoone.model.EmployeeProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil; // Убедись, что путь правильный
import com.carpooling.hibernate.ThreadLocalSessionContext; // Убедись, что путь правильный
import org.hibernate.LazyInitializationException;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("@OneToOne Shared Primary Key (using @MapsId) Strategy Tests")
class OneToOneSharedPkTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private GenericDao<Employee, Long> employeeDao;

    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() {
        // HibernateTestUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        employeeDao = new GenericDao<>(sessionFactory, Employee.class);

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

    @Test
    @DisplayName("Save Employee with Profile: Success")
    void saveEmployeeWithProfile_Success() {
        Employee employee = new Employee("john.doe@example.com", "John Doe");
        EmployeeProfile profile = new EmployeeProfile("http://example.com/john.jpg", "Experienced developer.");
        employee.setProfile(profile); // Устанавливаем двунаправленную связь

        employeeDao.save(employee); // CascadeType.ALL должен сохранить и профиль
        session.flush();
        Long employeeId = employee.getId();
        Long profileId = profile.getId(); // ID профиля должен быть таким же, как у Employee
        session.clear();

        assertThat(employeeId).isNotNull().isPositive();
        assertThat(profileId).isNotNull().isEqualTo(employeeId); // Проверяем разделяемый PK

        Optional<Employee> foundEmployeeOpt = employeeDao.findById(employeeId);
        assertThat(foundEmployeeOpt).isPresent();
        Employee foundEmployee = foundEmployeeOpt.get();

        assertThat(foundEmployee.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundEmployee.getProfile()).isNotNull();
        assertThat(foundEmployee.getProfile().getId()).isEqualTo(employeeId);
        assertThat(foundEmployee.getProfile().getPhotoUrl()).isEqualTo("http://example.com/john.jpg");
        assertThat(foundEmployee.getProfile().getEmployee().getId()).isEqualTo(employeeId); // Проверка обратной связи
    }

    @Test
    @DisplayName("Save Employee without Profile: Success")
    void saveEmployeeWithoutProfile_Success() {
        Employee employee = new Employee("jane.doe@example.com", "Jane Doe");
        // Профиль не устанавливаем

        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        session.clear();

        assertThat(employeeId).isNotNull().isPositive();
        Optional<Employee> foundEmployeeOpt = employeeDao.findById(employeeId);
        assertThat(foundEmployeeOpt).isPresent();
        assertThat(foundEmployeeOpt.get().getProfile()).isNull(); // Профиль должен быть null
    }

    @Test
    @DisplayName("Find Employee By Id: Profile Lazy Loaded")
    void findEmployeeById_ProfileLazyLoaded_Success() {
        Employee employee = new Employee("lazy.load@example.com", "Lazy Loader");
        EmployeeProfile profile = new EmployeeProfile("photo.png", "Bio for lazy load");
        employee.setProfile(profile);
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        session.clear(); // Важно для теста ленивой загрузки

        Optional<Employee> foundEmployeeOpt = employeeDao.findById(employeeId);
        assertThat(foundEmployeeOpt).isPresent();
        Employee foundEmployee = foundEmployeeOpt.get();

        // Закрываем сессию, чтобы проверить LazyInitializationException
        transaction.commit(); // Коммитим, чтобы изменения сохранились, если были
        ThreadLocalSessionContext.unbind();
        session.close();

    }

    @Test
    @DisplayName("Find Employee By Id: Profile Eager Loaded (Using JOIN FETCH)")
    void findEmployeeById_ProfileEagerLoaded_WithJoinFetch() {
        Employee employee = new Employee("eager.load@example.com", "Eager Beaver");
        EmployeeProfile profile = new EmployeeProfile("eager.jpg", "Eager bio");
        employee.setProfile(profile);
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        session.clear();

        // Загружаем с JOIN FETCH
        Employee foundEmployee = session.createQuery(
                        "SELECT e FROM Employee e LEFT JOIN FETCH e.profile WHERE e.id = :id", Employee.class)
                .setParameter("id", employeeId)
                .uniqueResult();

        assertThat(foundEmployee).isNotNull();

        // Закрываем сессию
        transaction.commit();
        ThreadLocalSessionContext.unbind();
        session.close();

        // Доступ к профилю НЕ должен вызывать исключение
        assertDoesNotThrow(() -> {
            assertThat(foundEmployee.getProfile()).isNotNull();
            assertThat(foundEmployee.getProfile().getBiography()).isEqualTo("Eager bio");
        });
    }


    @Test
    @DisplayName("Update Employee: Add Profile to Existing Employee")
    void updateEmployee_AddProfile_Success() {
        Employee employee = new Employee("add.profile@example.com", "Add Profile Test");
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        session.clear();

        Employee employeeToUpdate = employeeDao.findById(employeeId).orElseThrow();
        EmployeeProfile newProfile = new EmployeeProfile("new_photo.jpg", "Profile added later.");
        employeeToUpdate.setProfile(newProfile); // Устанавливаем связь

        employeeDao.update(employeeToUpdate); // merge
        session.flush();
        session.clear();

        Employee updatedEmployee = employeeDao.findById(employeeId).orElseThrow();
        assertThat(updatedEmployee.getProfile()).isNotNull();
        assertThat(updatedEmployee.getProfile().getPhotoUrl()).isEqualTo("new_photo.jpg");
        assertThat(updatedEmployee.getProfile().getId()).isEqualTo(employeeId); // Проверяем ID профиля
    }

    @Test
    @DisplayName("Update Employee: Change Profile Details")
    void updateEmployee_ChangeProfileDetails_Success() {
        Employee employee = new Employee("change.profile@example.com", "Change Profile");
        EmployeeProfile profile = new EmployeeProfile("initial.jpg", "Initial bio");
        employee.setProfile(profile);
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        session.clear();

        Employee employeeToUpdate = employeeDao.findById(employeeId).orElseThrow();
        assertThat(employeeToUpdate.getProfile()).isNotNull();
        employeeToUpdate.getProfile().setBiography("Updated biography!");

        employeeDao.update(employeeToUpdate);
        session.flush();
        session.clear();

        Employee updatedEmployee = employeeDao.findById(employeeId).orElseThrow();
        assertThat(updatedEmployee.getProfile().getBiography()).isEqualTo("Updated biography!");
    }

    @Test
    @DisplayName("Update Employee: Remove Profile (orphanRemoval)")
    void updateEmployee_RemoveProfile_OrphanRemoval_Success() {
        Employee employee = new Employee("remove.profile@example.com", "Remove Profile");
        EmployeeProfile profile = new EmployeeProfile("temp.jpg", "Temporary bio");
        employee.setProfile(profile);
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        Long profileId = profile.getId(); // Сохраняем ID профиля для проверки удаления
        session.clear();

        Employee employeeToUpdate = employeeDao.findById(employeeId).orElseThrow();
        assertThat(employeeToUpdate.getProfile()).isNotNull();
        employeeToUpdate.setProfile(null); // Удаляем связь, orphanRemoval=true должен удалить профиль

        employeeDao.update(employeeToUpdate);
        session.flush(); // Здесь должен удалиться профиль из employee_profiles_oto
        session.clear();

        Employee updatedEmployee = employeeDao.findById(employeeId).orElseThrow();
        assertThat(updatedEmployee.getProfile()).isNull();

        // Проверяем, что профиль действительно удален из своей таблицы
        EmployeeProfile deletedProfile = session.get(EmployeeProfile.class, profileId);
        assertThat(deletedProfile).isNull();
    }


    @Test
    @DisplayName("Delete Employee: Success (Cascade Delete Profile)")
    void deleteEmployee_WithProfile_CascadeDelete_Success() {
        Employee employee = new Employee("delete.cascade@example.com", "Delete Cascade");
        EmployeeProfile profile = new EmployeeProfile("delete.jpg", "Bio to be deleted");
        employee.setProfile(profile);
        employeeDao.save(employee);
        session.flush();
        Long employeeId = employee.getId();
        Long profileId = profile.getId();
        session.clear();

        assertThat(employeeDao.findById(employeeId)).isPresent(); // Убедимся, что существует

        employeeDao.deleteById(employeeId);
        session.flush();
        session.clear();

        assertThat(employeeDao.findById(employeeId)).isNotPresent();
        // Проверяем, что профиль также удален каскадно
        EmployeeProfile deletedProfile = session.get(EmployeeProfile.class, profileId);
        assertThat(deletedProfile).isNull();
    }

    @Test
    @DisplayName("Delete Employee: Failure (Not Found, should do nothing)")
    void deleteEmployeeById_NotFound_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            employeeDao.deleteById(-997L);
            session.flush();
        });
    }
}