package cli;

import com.man.Main;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "profile", description = "Управление профилем пользователя")
public class ProfileCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Command(name = "view", description = "Просмотр профиля пользователя")
    public void viewProfile(@CommandLine.Option(names = "--user-id", required = true) int userId) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            var user = provider.getUserById(userId);
            if (user != null) {
                System.out.println(user);
            } else {
                System.err.println("Пользователь не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка просмотра профиля: " + e.getMessage());
        }
    }

    @CommandLine.Command(name = "edit", description = "Редактирование профиля пользователя")
    public void editProfile(
            @CommandLine.Option(names = "--user-id", required = true) int userId,
            @CommandLine.Option(names = "--name", description = "Новое имя") String name,
            @CommandLine.Option(names = "--email", description = "Новая почта") String email,
            @CommandLine.Option(names = "--phone", description = "Новый телефон") String phone,
            @CommandLine.Option(names = "--address", description = "Новый адрес") String address
    ) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            var user = provider.getUserById(userId);
            if (user == null) {
                System.err.println("Пользователь не найден.");
                return;
            }
            if (name != null) user.setName(name);
            if (email != null) user.setEmail(email);
            if (phone != null) user.setPhone(phone);
            if (address != null) user.setAddress(address);
            provider.updateUser(user);
            System.out.println("Профиль успешно обновлен.");
        } catch (SQLException e) {
            System.err.println("Ошибка редактирования профиля: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Используйте `profile --help` для просмотра доступных команд.");
    }
}

