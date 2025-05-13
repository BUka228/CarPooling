package inheritance.tableperclass;

import inheritance.common.GenericVehicleDao; // Путь к Generic DAO
import inheritance.tableperclass.model.CarTablePerClass;
import inheritance.tableperclass.model.MotorcycleTablePerClass;
import inheritance.tableperclass.model.VehicleTablePerClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil; // Убедись, что путь правильный
import com.carpooling.hibernate.ThreadLocalSessionContext; // Убедись, что путь правильный

import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException; // Для проверки NOT NULL

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class TablePerClassStrategyTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    // DAO для каждого типа
    private GenericVehicleDao<VehicleTablePerClass, Long> vehicleDao;
    private GenericVehicleDao<CarTablePerClass, Long> carDao;
    private GenericVehicleDao<MotorcycleTablePerClass, Long> motorcycleDao;

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
        vehicleDao = new GenericVehicleDao<>(sessionFactory, VehicleTablePerClass.class);
        carDao = new GenericVehicleDao<>(sessionFactory, CarTablePerClass.class);
        motorcycleDao = new GenericVehicleDao<>(sessionFactory, MotorcycleTablePerClass.class);

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

    // --- Helper Methods to create entities ---
    private CarTablePerClass createCar(String manufacturer, int year, int doors, boolean ac) {
        return new CarTablePerClass(manufacturer, year, doors, ac);
    }

    private MotorcycleTablePerClass createMotorcycle(String manufacturer, int year, boolean sidecar, int cc) {
        return new MotorcycleTablePerClass(manufacturer, year, sidecar, cc);
    }


    // --- VehicleTablePerClass Tests (включая полиморфные) ---
    @Nested
    @DisplayName("VehicleTablePerClass (Base & Polymorphic) Operations")
    class VehicleTpcTests {

        @Test
        @DisplayName("Save Vehicle (Car): Success")
        void saveVehicle_CarInstance_Success() {
            CarTablePerClass car = createCar("VehicleSaveCar", 2023, 4, true);
            VehicleTablePerClass savedVehicle = vehicleDao.save(car); // Сохраняем Car через Vehicle DAO
            session.flush();

            assertThat(savedVehicle).isNotNull();
            assertThat(savedVehicle.getId()).isNotNull().isPositive();
            assertThat(savedVehicle.getManufacturer()).isEqualTo("VehicleSaveCar");
            assertThat(savedVehicle).isInstanceOf(CarTablePerClass.class); // Проверяем реальный тип
        }

        @Test
        @DisplayName("Save Vehicle: Failure (Null Manufacturer on Car)")
        void saveVehicle_Car_NullManufacturer_Failure() {
            CarTablePerClass car = createCar(null, 2023, 4, true);


            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                vehicleDao.save(car);
                session.flush();
            });
            boolean foundPVE = false;
            while (rootCause != null) {
                if (rootCause instanceof PropertyValueException && rootCause.getMessage().contains("manufacturer")) {
                    foundPVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundPVE).isTrue();
        }


        @Test
        @DisplayName("Find Vehicle By Id: Success (Car instance)")
        void findVehicleById_CarInstance_Success() {
            CarTablePerClass car = createCar("FindCarViaVehicle", 2022, 2, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            Optional<VehicleTablePerClass> foundVehicleOpt = vehicleDao.findById(carId);
            assertThat(foundVehicleOpt).isPresent();
            assertThat(foundVehicleOpt.get()).isInstanceOf(CarTablePerClass.class);
            assertThat(foundVehicleOpt.get().getManufacturer()).isEqualTo("FindCarViaVehicle");
        }

        @Test
        @DisplayName("Find Vehicle By Id: Failure (Not Found)")
        void findVehicleById_NotFound_Failure() {
            Optional<VehicleTablePerClass> foundVehicleOpt = vehicleDao.findById(-999L);
            assertThat(foundVehicleOpt).isNotPresent();
        }

        @Test
        @DisplayName("Update Vehicle (Car): Success")
        void updateVehicle_CarInstance_Success() {
            CarTablePerClass car = createCar("UpdateCarViaVehicle", 2021, 4, true);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.evict(car); // Detach

            VehicleTablePerClass vehicleToUpdate = vehicleDao.findById(carId).orElseThrow();
            vehicleToUpdate.setManufacturer("UpdatedCarManufacturer");
            ((CarTablePerClass) vehicleToUpdate).setNumberOfDoors(3); // Кастуем для специфичного поля

            VehicleTablePerClass updatedVehicle = vehicleDao.update(vehicleToUpdate);
            session.flush();
            session.clear();

            assertThat(updatedVehicle.getManufacturer()).isEqualTo("UpdatedCarManufacturer");
            assertThat(((CarTablePerClass)updatedVehicle).getNumberOfDoors()).isEqualTo(3);

            CarTablePerClass foundCar = carDao.findById(carId).orElseThrow(); // Проверяем через CarDAO
            assertThat(foundCar.getManufacturer()).isEqualTo("UpdatedCarManufacturer");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(3);
        }

        @Test
        @DisplayName("Update Vehicle: Failure (Null Manufacturer on Motorcycle)")
        void updateVehicle_Motorcycle_NullManufacturer_Failure() {
            MotorcycleTablePerClass moto = createMotorcycle("UpdateFailMoto", 2020, false, 600);
            vehicleDao.save(moto);
            session.flush();
            Long motoId = moto.getId();
            session.evict(moto);

            VehicleTablePerClass motoToUpdate = vehicleDao.findById(motoId).orElseThrow();
            motoToUpdate.setManufacturer(null);

            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                vehicleDao.update(motoToUpdate);
                session.flush();
            });
            boolean foundPVE = false;
            while (rootCause != null) {
                if (rootCause instanceof PropertyValueException && rootCause.getMessage().contains("manufacturer")) {
                    foundPVE = true;
                    break;
                }
                if (rootCause == rootCause.getCause()) break;
                rootCause = rootCause.getCause();
            }
            assertThat(foundPVE).isTrue();
        }

        @Test
        @DisplayName("Delete Vehicle By Id (Car): Success")
        void deleteVehicleById_CarInstance_Success() {
            CarTablePerClass car = createCar("DeleteCarViaVehicle", 2018, 4, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            assertThat(vehicleDao.findById(carId)).isPresent();
            vehicleDao.deleteById(carId);
            session.flush();
            session.clear();

            assertThat(vehicleDao.findById(carId)).isNotPresent();
            assertThat(carDao.findById(carId)).isNotPresent(); // Проверяем и через CarDAO
        }

        @Test
        @DisplayName("Delete Vehicle By Id: Failure (Not Found)")
        void deleteVehicleById_NotFound_Failure() {
            assertDoesNotThrow(() -> {
                vehicleDao.deleteById(-998L);
                session.flush();
            });
        }

        @Test
        @DisplayName("Find All Vehicles (Polymorphic): Success")
        void findAllVehicles_Polymorphic_ShouldReturnAllTypes() {
            CarTablePerClass car1 = createCar("PolyCar1", 2023, 4, true);
            MotorcycleTablePerClass moto1 = createMotorcycle("PolyMoto1", 2022, false, 1200);
            vehicleDao.save(car1);
            vehicleDao.save(moto1);
            session.flush();
            session.clear();

            List<VehicleTablePerClass> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().hasSize(2);
            assertThat(vehicles).extracting("manufacturer").containsExactlyInAnyOrder("PolyCar1", "PolyMoto1");
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(CarTablePerClass.class));
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(MotorcycleTablePerClass.class));
        }

        @Test
        @DisplayName("Find All Vehicles: Success (Empty)")
        void findAllVehicles_Empty_Success() {
            List<VehicleTablePerClass> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().isEmpty();
        }
    }

    // --- CarTablePerClass Specific Tests ---
    @Nested
    @DisplayName("CarTablePerClass Specific Operations")
    class CarTpcTests {
        @Test
        @DisplayName("Save and Find Car: Success")
        void saveAndFindCar_Success() {
            CarTablePerClass car = createCar("Ford", 2023, 4, true);
            carDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            assertThat(carId).isNotNull();
            Optional<CarTablePerClass> foundCarOpt = carDao.findById(carId);

            assertThat(foundCarOpt).isPresent();
            CarTablePerClass foundCar = foundCarOpt.get();
            assertThat(foundCar.getManufacturer()).isEqualTo("Ford");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(4);
        }

        @Test
        @DisplayName("Find All Cars: Success (Only Cars)")
        void findAllCars_ShouldReturnOnlyCars() {
            carDao.save(createCar("CarOnly1", 2023, 2, true));
            motorcycleDao.save(createMotorcycle("MotoForCarTest", 2022, false, 600)); // Этот не должен попасть
            carDao.save(createCar("CarOnly2", 2021, 4, false));
            session.flush();
            session.clear();

            List<CarTablePerClass> cars = carDao.findAll();
            assertThat(cars).isNotNull().hasSize(2);
            assertThat(cars).extracting(CarTablePerClass::getManufacturer).containsExactlyInAnyOrder("CarOnly1", "CarOnly2");
        }
    }

    // --- MotorcycleTablePerClass Specific Tests ---
    @Nested
    @DisplayName("MotorcycleTablePerClass Specific Operations")
    class MotorcycleTpcTests {
        @Test
        @DisplayName("Save and Find Motorcycle: Success")
        void saveAndFindMotorcycle_Success() {
            MotorcycleTablePerClass moto = createMotorcycle("BMW", 2022, true, 1250);
            motorcycleDao.save(moto);
            session.flush();
            Long motoId = moto.getId();
            session.clear();

            assertThat(motoId).isNotNull();
            Optional<MotorcycleTablePerClass> foundMotoOpt = motorcycleDao.findById(motoId);

            assertThat(foundMotoOpt).isPresent();
            MotorcycleTablePerClass foundMoto = foundMotoOpt.get();
            assertThat(foundMoto.getManufacturer()).isEqualTo("BMW");
            assertThat(foundMoto.isHasSidecar()).isTrue();
        }

        @Test
        @DisplayName("Find All Motorcycles: Success (Only Motorcycles)")
        void findAllMotorcycles_ShouldReturnOnlyMotorcycles() {
            carDao.save(createCar("CarForMotoTest", 2023, 2, true)); // Этот не должен попасть
            motorcycleDao.save(createMotorcycle("MotoOnly1", 2022, false, 600));
            motorcycleDao.save(createMotorcycle("MotoOnly2", 2021, true, 1000));
            session.flush();
            session.clear();

            List<MotorcycleTablePerClass> motos = motorcycleDao.findAll();
            assertThat(motos).isNotNull().hasSize(2);
            assertThat(motos).extracting(MotorcycleTablePerClass::getManufacturer).containsExactlyInAnyOrder("MotoOnly1", "MotoOnly2");
        }
    }
}