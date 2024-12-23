package cli;

import com.man.Main;
import model.database.User;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "register", description = "Регистрация нового пользователя")
public class RegisterUserCommand implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Option(names = "--name", required = true, description = "Полное имя пользователя")
    private String name;

    @CommandLine.Option(names = "--email", required = true, description = "Электронная почта пользователя")
    private String email;

    @CommandLine.Option(names = "--password", required = true, description = "Пароль пользователя")
    private String password;

    @CommandLine.Option(names = "--gender", description = "Пол пользователя")
    private String gender;

    @CommandLine.Option(names = "--phone", description = "Номер телефона пользователя")
    private String phone;

    @CommandLine.Option(names = "--birthdate", required = true, description = "Дата рождения пользователя (формат: yyyy-MM-dd)")
    private String birthDate;

    @CommandLine.Option(names = "--address", description = "Адрес пользователя")
    private String address;

    @CommandLine.Option(names = "--preferences", description = "Предпочтения пользователя")
    private String preferences;

    @Override
    public void run() {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            User user = new User(
                    0, // ID будет автоматически сгенерирован
                    name,
                    email,
                    password,
                    gender,
                    phone,
                    java.sql.Date.valueOf(birthDate),
                    address,
                    preferences
            );
            provider.createUser(user);
            System.out.println("Пользователь успешно зарегистрирован.");
        } catch (SQLException e) {
            System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
        }
    }
}

