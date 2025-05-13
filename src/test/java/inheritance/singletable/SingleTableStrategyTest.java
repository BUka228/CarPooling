package inheritance.singletable;

import inheritance.common.GenericDao; // Путь к Generic DAO
import inheritance.singletable.model.CarSingleTable;
import inheritance.singletable.model.MotorcycleSingleTable;
import inheritance.singletable.model.VehicleSingleTable;
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


class SingleTableStrategyTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    // DAO для каждого типа
    private GenericDao<VehicleSingleTable, Long> vehicleDao;
    private GenericDao<CarSingleTable, Long> carDao;
    private GenericDao<MotorcycleSingleTable, Long> motorcycleDao;

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
        vehicleDao = new GenericDao<>(sessionFactory, VehicleSingleTable.class);
        carDao = new GenericDao<>(sessionFactory, CarSingleTable.class);
        motorcycleDao = new GenericDao<>(sessionFactory, MotorcycleSingleTable.class);

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
    private CarSingleTable createCar(String manufacturer, int year, Integer doors, Boolean ac) {
        return new CarSingleTable(manufacturer, year, doors, ac);
    }

    private MotorcycleSingleTable createMotorcycle(String manufacturer, int year, Boolean sidecar, Integer cc) {
        return new MotorcycleSingleTable(manufacturer, year, sidecar, cc);
    }


    // --- VehicleSingleTable Tests (включая полиморфные) ---
    @Nested
    @DisplayName("VehicleSingleTable (Base & Polymorphic) Operations")
    class VehicleStTests {

        @Test
        @DisplayName("Save Vehicle (Car): Success")
        void saveVehicle_CarInstance_Success() {
            CarSingleTable car = createCar("VehicleSaveCarST", 2023, 4, true);
            VehicleSingleTable savedVehicle = vehicleDao.save(car);
            session.flush();

            assertThat(savedVehicle).isNotNull();
            assertThat(savedVehicle.getId()).isNotNull().isPositive();
            assertThat(savedVehicle.getManufacturer()).isEqualTo("VehicleSaveCarST");
            assertThat(savedVehicle).isInstanceOf(CarSingleTable.class);

            // Проверка дискриминатора (если есть доступ к сессии напрямую или через Native Query)
            // в реальном тесте может быть избыточно
            session.clear(); // Очищаем, чтобы точно пойти в БД
            Object discValue = session.createNativeQuery("SELECT vehicle_type FROM vehicles_single_table WHERE id = :id")
                    .setParameter("id", savedVehicle.getId())
                    .uniqueResult();
            assertThat(discValue).isEqualTo("CAR");
        }

        @Test
        @DisplayName("Save Vehicle: Failure (Null Manufacturer on Car)")
        void saveVehicle_Car_NullManufacturer_Failure() {
            CarSingleTable car = createCar(null, 2023, 4, true);

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
            CarSingleTable car = createCar("FindCarST", 2022, 2, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            Optional<VehicleSingleTable> foundVehicleOpt = vehicleDao.findById(carId);
            assertThat(foundVehicleOpt).isPresent();
            assertThat(foundVehicleOpt.get()).isInstanceOf(CarSingleTable.class);
            assertThat(foundVehicleOpt.get().getManufacturer()).isEqualTo("FindCarST");
            assertThat(((CarSingleTable)foundVehicleOpt.get()).getNumberOfDoors()).isEqualTo(2);
        }

        @Test
        @DisplayName("Find Vehicle By Id: Failure (Not Found)")
        void findVehicleById_NotFound_Failure() {
            Optional<VehicleSingleTable> foundVehicleOpt = vehicleDao.findById(-999L);
            assertThat(foundVehicleOpt).isNotPresent();
        }

        @Test
        @DisplayName("Update Vehicle (Car): Success")
        void updateVehicle_CarInstance_Success() {
            CarSingleTable car = createCar("UpdateCarST", 2021, 4, true);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.evict(car);

            VehicleSingleTable vehicleToUpdate = vehicleDao.findById(carId).orElseThrow();
            vehicleToUpdate.setManufacturer("UpdatedCarManufacturerST");
            ((CarSingleTable) vehicleToUpdate).setNumberOfDoors(3);

            VehicleSingleTable updatedVehicle = vehicleDao.update(vehicleToUpdate);
            session.flush();
            session.clear();

            assertThat(updatedVehicle.getManufacturer()).isEqualTo("UpdatedCarManufacturerST");
            assertThat(((CarSingleTable)updatedVehicle).getNumberOfDoors()).isEqualTo(3);

            CarSingleTable foundCar = carDao.findById(carId).orElseThrow();
            assertThat(foundCar.getManufacturer()).isEqualTo("UpdatedCarManufacturerST");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(3);
        }

        @Test
        @DisplayName("Update Vehicle: Failure (Null modelYear on Motorcycle)")
        void updateVehicle_Motorcycle_NullModelYear_Failure() {
            // modelYear - примитив, но если бы был Integer и nullable=false
            MotorcycleSingleTable moto = createMotorcycle("UpdateFailMotoST", 2020, false, 600);
            vehicleDao.save(moto);
            session.flush();
            Long motoId = moto.getId();
            session.evict(moto);

            VehicleSingleTable motoToUpdate = vehicleDao.findById(motoId).orElseThrow();

            // Для примера, если бы manufacturer был null:
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
            CarSingleTable car = createCar("DeleteCarST", 2018, 4, false);
            vehicleDao.save(car);
            session.flush();
            Long carId = car.getId();
            session.clear();

            assertThat(vehicleDao.findById(carId)).isPresent();
            vehicleDao.deleteById(carId);
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
            CarSingleTable car1 = createCar("PolyCarST1", 2023, 4, true);
            MotorcycleSingleTable moto1 = createMotorcycle("PolyMotoST1", 2022, false, 1200);
            vehicleDao.save(car1);
            vehicleDao.save(moto1);
            session.flush();
            session.clear();

            List<VehicleSingleTable> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().hasSize(2);
            assertThat(vehicles).extracting("manufacturer").containsExactlyInAnyOrder("PolyCarST1", "PolyMotoST1");
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(CarSingleTable.class));
            assertThat(vehicles).anySatisfy(v -> assertThat(v).isInstanceOf(MotorcycleSingleTable.class));
        }

        @Test
        @DisplayName("Find All Vehicles: Success (Empty)")
        void findAllVehicles_Empty_Success() {
            List<VehicleSingleTable> vehicles = vehicleDao.findAll();
            assertThat(vehicles).isNotNull().isEmpty();
        }
    }

    // --- CarSingleTable Specific Tests ---
    @Nested
    @DisplayName("CarSingleTable Specific Operations")
    class CarStTests {
        @Test
        @DisplayName("Save and Find Car: Success")
        void saveAndFindCar_Success() {
            CarSingleTable car = createCar("FordST", 2023, 4, true);
            carDao.save(car); // Используем carDao
            session.flush();
            Long carId = car.getId();
            session.clear();

            Optional<CarSingleTable> foundCarOpt = carDao.findById(carId);
            assertThat(foundCarOpt).isPresent();
            CarSingleTable foundCar = foundCarOpt.get();
            assertThat(foundCar.getManufacturer()).isEqualTo("FordST");
            assertThat(foundCar.getNumberOfDoors()).isEqualTo(4);
        }

        @Test
        @DisplayName("Find All Cars: Success (Only Cars)")
        void findAllCars_ShouldReturnOnlyCars() {
            carDao.save(createCar("CarOnlyST1", 2023, 2, true));
            // Сохраняем мотоцикл через vehicleDao, чтобы он был в той же таблице, но carDao его не нашел
            vehicleDao.save(createMotorcycle("MotoForCarTestST", 2022, false, 600));
            carDao.save(createCar("CarOnlyST2", 2021, 4, false));
            session.flush();
            session.clear();

            List<CarSingleTable> cars = carDao.findAll(); // carDao должен вернуть только автомобили
            assertThat(cars).isNotNull().hasSize(2);
            assertThat(cars).extracting(CarSingleTable::getManufacturer).containsExactlyInAnyOrder("CarOnlyST1", "CarOnlyST2");
        }
    }

    // --- MotorcycleSingleTable Specific Tests ---
    @Nested
    @DisplayName("MotorcycleSingleTable Specific Operations")
    class MotorcycleStTests {
        @Test
        @DisplayName("Save and Find Motorcycle: Success")
        void saveAndFindMotorcycle_Success() {
            MotorcycleSingleTable moto = createMotorcycle("BMW_ST", 2022, true, 1250);
            motorcycleDao.save(moto);
            session.flush();
            Long motoId = moto.getId();
            session.clear();

            Optional<MotorcycleSingleTable> foundMotoOpt = motorcycleDao.findById(motoId);
            assertThat(foundMotoOpt).isPresent();
            MotorcycleSingleTable foundMoto = foundMotoOpt.get();
            assertThat(foundMoto.getManufacturer()).isEqualTo("BMW_ST");
            assertThat(foundMoto.getEngineDisplacementCC()).isEqualTo(1250);
        }

        @Test
        @DisplayName("Find All Motorcycles: Success (Only Motorcycles)")
        void findAllMotorcycles_ShouldReturnOnlyMotorcycles() {
            // Сохраняем авто через vehicleDao, чтобы он был в той же таблице, но motorcycleDao его не нашел
            vehicleDao.save(createCar("CarForMotoTestST", 2023, 2, true));
            motorcycleDao.save(createMotorcycle("MotoOnlyST1", 2022, false, 600));
            motorcycleDao.save(createMotorcycle("MotoOnlyST2", 2021, true, 1000));
            session.flush();
            session.clear();

            List<MotorcycleSingleTable> motos = motorcycleDao.findAll();
            assertThat(motos).isNotNull().hasSize(2);
            assertThat(motos).extracting(MotorcycleSingleTable::getManufacturer).containsExactlyInAnyOrder("MotoOnlyST1", "MotoOnlyST2");
        }
    }
}