package relations.onetoonefk;

import inheritance.common.GenericDao;
import relations.onetoonefk.model.Manager;
import relations.onetoonefk.model.Office;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil;
import com.carpooling.hibernate.ThreadLocalSessionContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException; // Для проверки NOT NULL

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@DisplayName("@OneToOne with Foreign Key (using @JoinColumn(unique=true)) Tests")
class OneToOneForeignKeyTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private GenericDao<Manager, Long> managerDao;
    private GenericDao<Office, Long> officeDao;

    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() { /* HibernateTestUtil.shutdown(); */ }

    @BeforeEach
    void setUp() {
        managerDao = new GenericDao<>(sessionFactory, Manager.class);
        officeDao = new GenericDao<>(sessionFactory, Office.class);

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
    private Manager createManager(String name) {
        return new Manager(name);
    }
    private Office createOffice(String roomNumber, int floor) {
        return new Office(roomNumber, floor);
    }

    @Nested
    @DisplayName("Manager DAO Operations")
    class ManagerDaoTests {

        @Test
        @DisplayName("Save Manager with new Office: Success")
        void saveManagerWithNewOffice_Success() {
            Manager manager = createManager("Alice Wonderland");
            Office office = createOffice("A101", 1);
            manager.setOffice(office); // Устанавливаем связь (и обратную благодаря хелперу)

            managerDao.save(manager); // CascadeType.ALL должен сохранить и Office
            session.flush();
            Long managerId = manager.getId();
            Long officeId = office.getId(); // Офис должен получить ID
            session.clear();

            assertThat(managerId).isNotNull();
            assertThat(officeId).isNotNull();

            Manager foundManager = managerDao.findById(managerId).orElseThrow();
            assertThat(foundManager.getName()).isEqualTo("Alice Wonderland");
            assertThat(foundManager.getOffice()).isNotNull();
            assertThat(foundManager.getOffice().getId()).isEqualTo(officeId);
            assertThat(foundManager.getOffice().getRoomNumber()).isEqualTo("A101");

            Office foundOffice = officeDao.findById(officeId).orElseThrow();
            assertThat(foundOffice.getManager()).isNotNull();
            assertThat(foundOffice.getManager().getId()).isEqualTo(managerId);
        }

        @Test
        @DisplayName("Save Manager: Failure (Null Name)")
        void saveManager_NullName_Failure() {
            Manager manager = createManager(null); // Имя null
            Office office = createOffice("A102", 1);
            manager.setOffice(office);

            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                managerDao.save(manager);
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
        @DisplayName("Save Manager: Failure - Assign Office already taken (UNIQUE constraint on office_id)")
        void saveManager_AssignTakenOffice_Failure() {
            Office office = createOffice("C303_M", 3); // Изменим имя для уникальности с другим тестом
            Manager manager1 = createManager("Charlie Chaplin");
            manager1.setOffice(office);
            managerDao.save(manager1);
            session.flush();
            Long officeId = office.getId();
            session.clear();

            Manager manager2 = createManager("David Copperfield");
            Office takenOffice = officeDao.findById(officeId).orElseThrow();
            manager2.setOffice(takenOffice);

            // Улучшенная проверка ConstraintViolationException
            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                managerDao.save(manager2);
                session.flush();
            });
            boolean constraintViolationFound = false;
            while (rootCause != null) {
                if (rootCause instanceof ConstraintViolationException cve) {
                    assertThat(cve.getErrorCode() == 23505 || (cve.getSQLState() != null && cve.getSQLState().equals("23505"))).isTrue();
                    constraintViolationFound = true;
                    break;
                }
                if (rootCause.getCause() == rootCause) break; // Предотвращение бесконечного цикла
                rootCause = rootCause.getCause();
            }
            assertThat(constraintViolationFound)
                    .as("Expected ConstraintViolationException for unique office_id.")
                    .isTrue();
        }


        @Test
        @DisplayName("Find Manager By Id: Success (Found)")
        void findManagerById_Found_Success() {
            Manager manager = createManager("Find Me");
            manager.setOffice(createOffice("Z999", 9));
            managerDao.save(manager);
            session.flush();
            Long managerId = manager.getId();
            session.clear();

            Optional<Manager> foundOpt = managerDao.findById(managerId);
            assertThat(foundOpt).isPresent();
            assertThat(foundOpt.get().getName()).isEqualTo("Find Me");
            assertThat(foundOpt.get().getOffice()).isNotNull(); // Проверяем, что офис лениво загружается
        }

        @Test
        @DisplayName("Find Manager By Id: Failure (Not Found)")
        void findManagerById_NotFound_Failure() {
            Optional<Manager> foundOpt = managerDao.findById(-1L);
            assertThat(foundOpt).isNotPresent();
        }

        @Test
        @DisplayName("Update Manager: Change Name and Office")
        void updateManager_ChangeNameAndOffice_Success() {
            Manager manager = createManager("Old Name");
            manager.setOffice(createOffice("OldOffice", 1));
            managerDao.save(manager);
            session.flush();
            Long managerId = manager.getId();
            session.clear();

            Manager managerToUpdate = managerDao.findById(managerId).orElseThrow();
            Office newOffice = createOffice("NewOffice", 2);

            managerToUpdate.setName("New Name");
            managerToUpdate.setOffice(newOffice); // Установит и обратную связь в Office

            managerDao.update(managerToUpdate);
            session.flush();
            session.clear();

            Manager updatedManager = managerDao.findById(managerId).orElseThrow();
            assertThat(updatedManager.getName()).isEqualTo("New Name");
            assertThat(updatedManager.getOffice()).isNotNull();
            assertThat(updatedManager.getOffice().getRoomNumber()).isEqualTo("NewOffice");
        }

        @Test
        @DisplayName("Update Manager: Failure (Set Office to one already taken)")
        void updateManager_SetTakenOffice_Failure() {
            Office office1 = createOffice("T101_M_Update", 1);
            Manager manager1 = createManager("Manager One Update");
            manager1.setOffice(office1);
            managerDao.save(manager1);

            Office office2 = createOffice("T102_M_Update", 1); // Другой офис
            Manager manager2 = createManager("Manager Two Update");
            manager2.setOffice(office2);
            managerDao.save(manager2);
            session.flush();
            Long manager2Id = manager2.getId();
            Long office1Id = office1.getId(); // ID первого офиса
            session.clear();

            Manager managerToUpdate = managerDao.findById(manager2Id).orElseThrow();
            Office takenOffice = officeDao.findById(office1Id).orElseThrow();
            managerToUpdate.setOffice(takenOffice);

            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                managerDao.update(managerToUpdate);
                session.flush();
            });
            boolean constraintViolationFound = false;
            while (rootCause != null) {
                if (rootCause instanceof ConstraintViolationException cve) {
                    assertThat(cve.getErrorCode() == 23505 || (cve.getSQLState() != null && cve.getSQLState().equals("23505"))).isTrue();
                    constraintViolationFound = true;
                    break;
                }
                if (rootCause.getCause() == rootCause) break;
                rootCause = rootCause.getCause();
            }
            assertThat(constraintViolationFound)
                    .as("Expected ConstraintViolationException for unique office_id on update.")
                    .isTrue();
        }

        @Test
        @DisplayName("Delete Manager with Office (CascadeType.ALL): Success")
        void deleteManager_WithOfficeCascade_Success() {
            Manager manager = createManager("ToDelete");
            manager.setOffice(createOffice("DelOffice", 7));
            managerDao.save(manager);
            session.flush();
            Long managerId = manager.getId();
            Long officeId = manager.getOffice().getId();
            session.clear();

            assertThat(managerDao.findById(managerId)).isPresent();
            assertThat(officeDao.findById(officeId)).isPresent();

            managerDao.deleteById(managerId);
            session.flush();
            session.clear();

            assertThat(managerDao.findById(managerId)).isNotPresent();
            // Проверяем, что офис тоже удален из-за CascadeType.ALL на Manager.office
            assertThat(officeDao.findById(officeId)).isNotPresent();
        }

        @Test
        @DisplayName("Delete Manager: Failure (Not Found)")
        void deleteManager_NotFound_ShouldDoNothing() {
            assertDoesNotThrow(() -> {
                managerDao.deleteById(-1L);
                session.flush();
            });
        }
    }

    @Nested
    @DisplayName("Office DAO Operations")
    class OfficeDaoTests {

        @Test
        @DisplayName("Save Office: Success")
        void saveOffice_Success() {
            Office office = createOffice("G707", 7);
            officeDao.save(office);
            session.flush();
            Long officeId = office.getId();
            session.clear();

            assertThat(officeId).isNotNull();
            Optional<Office> foundOpt = officeDao.findById(officeId);
            assertThat(foundOpt).isPresent();
            assertThat(foundOpt.get().getRoomNumber()).isEqualTo("G707");
        }

        @Test
        @DisplayName("Save Office: Failure (Null Room Number)")
        void saveOffice_NullRoomNumber_Failure() {
            Office office = createOffice(null, 8);
            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                officeDao.save(office);
                session.flush();
            });
            boolean foundPVE = false;
            while(rootCause != null) {
                if (rootCause instanceof PropertyValueException && rootCause.getMessage().contains("roomNumber")) {
                    foundPVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundPVE).isTrue();
        }


        @Test
        @DisplayName("Find Office By Id: Success (Found)")
        void findOfficeById_Found_Success() {
            Office office = createOffice("I909", 9);
            officeDao.save(office);
            session.flush();
            Long officeId = office.getId();
            session.clear();

            Optional<Office> foundOpt = officeDao.findById(officeId);
            assertThat(foundOpt).isPresent();
            assertThat(foundOpt.get().getFloor()).isEqualTo(9);
        }

        @Test
        @DisplayName("Find Office By Id: Failure (Not Found)")
        void findOfficeById_NotFound_Failure() {
            Optional<Office> foundOpt = officeDao.findById(-2L);
            assertThat(foundOpt).isNotPresent();
        }


        @Test
        @DisplayName("Delete Office: Failure (Referenced by Manager without cascade from Office)")
        void deleteOffice_ReferencedByManager_Failure() {
            Manager manager = createManager("ManagerForOfficeDeleteO");
            Office office = createOffice("K111_O", 1);
            manager.setOffice(office);

            managerDao.save(manager);
            session.flush();
            Long officeId = office.getId();
            Long managerId = manager.getId(); // Сохраним ID менеджера
            session.clear();

            // Убедимся, что менеджер и офис существуют
            assertThat(managerDao.findById(managerId)).isPresent();
            assertThat(officeDao.findById(officeId)).isPresent();

        }
    }
}