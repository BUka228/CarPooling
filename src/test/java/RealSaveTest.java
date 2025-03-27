import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.RegistrationException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.factories.ServiceFactory;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import com.carpooling.utils.HibernateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RealSaveTest {

    @Test
    public void testSaveBooking() {

    }
    /*@Test
    public void testSaveUser() throws RegistrationException {
        UserService userService = ServiceFactory.getUserService();

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setGender("Male");
        user.setPhone("+1234567890");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPreferences("No smoking");
        String userId = userService.registerUser(user);
        assertNotNull(userId);
    }*/
}