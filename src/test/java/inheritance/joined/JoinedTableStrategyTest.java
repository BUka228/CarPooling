package inheritance.joined;

import inheritance.common.GenericVehicleDao; // Путь к Generic DAO
import inheritance.joined.model.CarJoined;
import inheritance.joined.model.MotorcycleJoined;
import inheritance.joined.model.VehicleJoined;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil; // Убедись, что путь правильный
import com.carpooling.hibernate.ThreadLocalSessionContext; // Убедись, что путь правильный

import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JoinedTableStrategyTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    // DAO для каждого типа
    private GenericVehicleDao<VehicleJoined, Long> vehicleDao;
    private GenericVehicleDao<CarJoined, Long> carDao;
    private GenericVehicleDao<MotorcycleJoined, Long> motorcycleDao;

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
        vehicleDao = new GenericVehicleDao<>(sessionFactory, VehicleJoined.class);
        carDao = new GenericVehicleDao<>(sessionFactory, CarJoined.class);
        motorcycleDao = new GenericVehicleDao<>(sessionFactory, MotorcycleJoined.class);

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
    private CarJoined createCar(String manufacturer, int year, int doors, boolean ac) {
        return new CarJoined(manufacturer, year, doors, ac);
    }

    private MotorcycleJoined createMotorcycle(String manufacturer, int year, boolean sidecar, int cc) {
        return new MotorcycleJoined(manufacturer, year, sidecar, cc);
    }


    // --- VehicleJoined Tests (включая полиморфные) ---
    @Nested
    @DisplayName("VehicleJoined (Base & Polymorphic) Operations")
    class VehicleJtTests {

        @Test
        @DisplayName("Save Vehicle (Car): Success")
        void saveVehicle_CarInstance_Success() {
            CarJoined car = createCar("VehicleSaveCarJT", 2023, 4, true);
            VehicleJoined savedVehicle = vehicleDao.save(car);
            session.flush();

            assertThat(savedVehicle).isNotNull();
            assertThat(savedVehicle.getId()).isNotNull().isPositive();
            assertThat(savedVehicle.getManufacturer()).isEqualTo("VehicleSaveCarJT");
            assertThat(savedVehicle).isInstanceOf(CarJoined.class);
            // Проверка: Hibernate должен сделать INSERT в vehicles_joined_base и в cars_joined_subclass
        }

        @Test
        @DisplayName("Save Vehicle: Failure (Null Manufacturer on Car)")
        void saveVehicle_Car_NullManufacturer_Failure() {
            CarJoined car = createCar(null, 2023, 4, true);

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
            CarJoined car = createCar("FindCarJT", 2022, 2, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            Optional<VehicleJoined> foundVehicleOpt = vehicleDao.findById(carId);
            assertThat(foundVehicleOpt).isPresent();
            assertThat(foundVehicleOpt.get()).isInstanceOf(CarJoined.class);
            CarJoined foundCar = (CarJoined) foundVehicleOpt.get();
            assertThat(foundCar.getManufacturer()).isEqualTo("FindCarJT");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(2); // Проверяем поле подкласса
            // Hibernate выполнит JOIN для загрузки этих данных
        }

        @Test
        @DisplayName("Find Vehicle By Id: Failure (Not Found)")
        void findVehicleById_NotFound_Failure() {
            Optional<VehicleJoined> foundVehicleOpt = vehicleDao.findById(-999L);
            assertThat(foundVehicleOpt).isNotPresent();
        }

        @Test
        @DisplayName("Update Vehicle (Car): Success")
        void updateVehicle_CarInstance_Success() {
            CarJoined car = createCar("UpdateCarJT", 2021, 4, true);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.evict(car);

            VehicleJoined vehicleToUpdate = vehicleDao.findById(carId).orElseThrow();
            vehicleToUpdate.setManufacturer("UpdatedCarManufacturerJT");
            ((CarJoined) vehicleToUpdate).setNumberOfDoors(3);

            VehicleJoined updatedVehicle = vehicleDao.update(vehicleToUpdate);
            session.flush();
            session.clear();

            assertThat(updatedVehicle.getManufacturer()).isEqualTo("UpdatedCarManufacturerJT");
            assertThat(((CarJoined)updatedVehicle).getNumberOfDoors()).isEqualTo(3);

            CarJoined foundCar = carDao.findById(carId).orElseThrow();
            assertThat(foundCar.getManufacturer()).isEqualTo("UpdatedCarManufacturerJT");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(3);
        }

        @Test
        @DisplayName("Update Vehicle: Failure (Null number_of_doors on Car)")
        void updateVehicle_Car_NullSpecificField_Failure() {
            CarJoined car = createCar("UpdateFailCarJT", 2020, 4, true);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.evict(car);

            CarJoined carToUpdate = (CarJoined) vehicleDao.findById(carId).orElseThrow();
            carToUpdate.setManufacturer(null);

            Throwable rootCause = assertThrows(PersistenceException.class, () -> {
                vehicleDao.update(carToUpdate);
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
            CarJoined car = createCar("DeleteCarJT", 2018, 4, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            assertThat(vehicleDao.findById(carId)).isPresent();
            vehicleDao.deleteById(carId); // Удалит из обеих таблиц (base и subclass)
            session.flush();
            session.clear();

            assertThat(vehicleDao.findById(carId)).isNotPresent();
            assertThat(carDao.findById(carId)).isNotPresent();
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
            CarJoined car1 = createCar("PolyCarJT1", 2023, 4, true);
            MotorcycleJoined moto1 = createMotorcycle("PolyMotoJT1", 2022, false, 1200);
            vehicleDao.save(car1);
            vehicleDao.save(moto1);
            session.flush();
            session.clear();

            List<VehicleJoined> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().hasSize(2);
            assertThat(vehicles).extracting("manufacturer").containsExactlyInAnyOrder("PolyCarJT1", "PolyMotoJT1");
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(CarJoined.class));
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(MotorcycleJoined.class));
            // Hibernate будет делать OUTER JOIN для сбора данных
        }

        @Test
        @DisplayName("Find All Vehicles: Success (Empty)")
        void findAllVehicles_Empty_Success() {
            List<VehicleJoined> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().isEmpty();
        }
    }

    // --- CarJoined Specific Tests ---
    @Nested
    @DisplayName("CarJoined Specific Operations")
    class CarJtTests {
        @Test
        @DisplayName("Save and Find Car: Success")
        void saveAndFindCar_Success() {
            CarJoined car = createCar("FordJT", 2023, 4, true);
            carDao.save(car); // Используем carDao
            session.flush();
            Long carId = car.getId();
            session.clear();

            Optional<CarJoined> foundCarOpt = carDao.findById(carId);
            assertThat(foundCarOpt).isPresent();
            CarJoined foundCar = foundCarOpt.get();
            assertThat(foundCar.getManufacturer()).isEqualTo("FordJT");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(4);
        }

        @Test
        @DisplayName("Find All Cars: Success (Only Cars)")
        void findAllCars_ShouldReturnOnlyCars() {
            carDao.save(createCar("CarOnlyJT1", 2023, 2, true));
            vehicleDao.save(createMotorcycle("MotoForCarTestJT", 2022, false, 600));
            carDao.save(createCar("CarOnlyJT2", 2021, 4, false));
            session.flush();
            session.clear();

            List<CarJoined> cars = carDao.findAll();
            assertThat(cars).isNotNull().hasSize(2);
            assertThat(cars).extracting(CarJoined::getManufacturer).containsExactlyInAnyOrder("CarOnlyJT1", "CarOnlyJT2");
        }
    }

    // --- MotorcycleJoined Specific Tests ---
    @Nested
    @DisplayName("MotorcycleJoined Specific Operations")
    class MotorcycleJtTests {
        @Test
        @DisplayName("Save and Find Motorcycle: Success")
        void saveAndFindMotorcycle_Success() {
            MotorcycleJoined moto = createMotorcycle("BMW_JT", 2022, true, 1250);
            motorcycleDao.save(moto);
            session.flush();
            Long motoId = moto.getId();
            session.clear();

            Optional<MotorcycleJoined> foundMotoOpt = motorcycleDao.findById(motoId);
            assertThat(foundMotoOpt).isPresent();
            MotorcycleJoined foundMoto = foundMotoOpt.get();
            assertThat(foundMoto.getManufacturer()).isEqualTo("BMW_JT");
            assertThat(foundMoto.getEngineDisplacementCC()).isEqualTo(1250);
        }

        @Test
        @DisplayName("Find All Motorcycles: Success (Only Motorcycles)")
        void findAllMotorcycles_ShouldReturnOnlyMotorcycles() {
            vehicleDao.save(createCar("CarForMotoTestJT", 2023, 2, true));
            motorcycleDao.save(createMotorcycle("MotoOnlyJT1", 2022, false, 600));
            motorcycleDao.save(createMotorcycle("MotoOnlyJT2", 2021, true, 1000));
            session.flush();
            session.clear();

            List<MotorcycleJoined> motos = motorcycleDao.findAll();
            assertThat(motos).isNotNull().hasSize(2);
            assertThat(motos).extracting(MotorcycleJoined::getManufacturer).containsExactlyInAnyOrder("MotoOnlyJT1", "MotoOnlyJT2");
        }
    }
}
