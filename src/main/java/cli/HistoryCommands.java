package cli;

import com.man.Main;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@CommandLine.Command(name = "history", description = "Просмотр истории поездок")
public class HistoryCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Option(names = "--user-id", required = true, description = "Идентификатор пользователя")
    private int userId;

    @Override
    public void run() {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            var trips = List.of(Objects.requireNonNull(provider.getTripById(userId)));
            trips.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("Ошибка при получении истории поездок: " + e.getMessage());
        }
    }
}
