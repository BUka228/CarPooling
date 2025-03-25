import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.impl.RouteServiceImpl;
import com.carpooling.services.impl.TripServiceImpl;
import com.carpooling.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RealSaveTest {

    @Test
    public void testSaveUser() throws UserServiceException, TripServiceException {
        /*// Устанавливаем тип хранилища для Hibernate
        CliContext.setCurrentStorageType(CliContext.StorageType.POSTGRES);

        // Создаем объект User
        User user = new User();
        user.setId(UUID.randomUUID().toString()); // Генерируем уникальный ID
        user.setName("TestName");
        user.setEmail("TestEmail");
        user.setPassword("TestPassword");
        user.setGender("TestGender");
        user.setBirthDate(Date.valueOf("2004-04-17"));
        user.setAddress("TestAddress");
        user.setPreferences("TestPreferences");

        // Инициализация UserService с Hibernate DAO
        UserServiceImpl userService = new UserServiceImpl(DaoFactory.getUserDao(CliContext.getCurrentStorageType()));
        String userId = userService.registerUser(user);
        assertNotNull(userId, "User ID should not be null after registration");

        // Создаем объект Trip
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID().toString()); // Генерируем уникальный ID
        trip.setDepartureTime(Date.valueOf("2024-04-17"));
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(Date.valueOf("2024-04-17"));
        trip.setStatus("TestStatus");
        trip.setEditable(true);

        // Создаем объект Route
        String id = UUID.randomUUID().toString();
        String startPoint = "TestStartPoint";
        String endPoint = "TestEndPoint";

        // Преобразуем строки в кодировку IBM866 (если это требование сохраняется)
        byte[] idBytes866 = id.getBytes(Charset.forName("IBM866"));
        byte[] startPointBytes866 = startPoint.getBytes(Charset.forName("IBM866"));
        byte[] endPointBytes866 = endPoint.getBytes(Charset.forName("IBM866"));

        String id866 = new String(idBytes866, Charset.forName("IBM866"));
        String startPoint866 = new String(startPointBytes866, Charset.forName("IBM866"));
        String endPoint866 = new String(endPointBytes866, Charset.forName("IBM866"));

        Route route = new Route();
        route.setId(id866);
        route.setStartPoint(startPoint866);
        route.setEndPoint(endPoint866);
        route.setDate(Date.valueOf(LocalDate.now()));
        route.setEstimatedDuration((short) 100);

        // Инициализация RouteService и TripService с Hibernate DAO
        RouteServiceImpl routeService = new RouteServiceImpl(DaoFactory.getRouteDao(CliContext.getCurrentStorageType()));
        TripServiceImpl tripService = new TripServiceImpl(
                DaoFactory.getTripDao(CliContext.getCurrentStorageType()),
                routeService
        );

        // Создаем поездку с объектами Trip, Route и User
        String tripId = tripService.createTrip(trip, route, user);
        assertNotNull(tripId, "Trip ID should not be null after creation");

        System.out.println("User ID: " + userId);
        System.out.println("Trip ID: " + tripId);*/
    }

    @Test
    public void cliSaveFromCommand() {
        // Оставлено пустым, как в исходном коде
    }
}