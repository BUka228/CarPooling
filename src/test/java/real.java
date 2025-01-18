import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.impl.TripServiceImpl;
import com.carpooling.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.sql.Date;
import java.time.LocalDate;

public class real {
    @Test
    public void TestSaveUser() throws UserServiceException, TripServiceException {
        User user = new User(
                "1",
                "TestName",
                "TestEmail",
                "TestPassword",
                "TestGender",
                "TestPhone",
                java.sql.Date.valueOf("2004-04-17"),
                "TestAddress",
                "TestPreferences"
        );
        CliContext.setCurrentStorageType(CliContext.StorageType.POSTGRES);
        UserServiceImpl userService = new UserServiceImpl();
        String userId = userService.registerUser(user);

        Trip trip = new Trip(
                "1",
                java.sql.Date.valueOf("2024-04-17"),
                (byte) 4,
                java.sql.Date.valueOf("2024-04-17"),
                "TestStatus",
                true
        );

        String id = "1";
        String startPoint = "TestStartPoint";
        String endPoint = "TestEndPoint";

        // Преобразуем строки в кодировку 866
        byte[] idBytes866 = id.getBytes(Charset.forName("IBM866"));
        byte[] startPointBytes866 = startPoint.getBytes(Charset.forName("IBM866"));
        byte[] endPointBytes866 = endPoint.getBytes(Charset.forName("IBM866"));

        // Если нужно преобразовать обратно в строку (для проверки)
        String id866 = new String(idBytes866, Charset.forName("IBM866"));
        String startPoint866 = new String(startPointBytes866, Charset.forName("IBM866"));
        String endPoint866 = new String(endPointBytes866, Charset.forName("IBM866"));

        // Создаем объект Route
        Route route = new Route(
                id866,
                startPoint866,
                endPoint866,
                Date.valueOf(LocalDate.now()),
                (short) 100
        );


        TripServiceImpl tripService = new TripServiceImpl();
        String tripId = tripService.createTrip(trip, route, userId);

    }


    @Test
    public void CliSaveFromCommand()  {

    }
}
