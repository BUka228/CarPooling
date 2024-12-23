package cli;


import com.man.Main;
import model.database.Rating;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "rate", description = "Оценить поездку")
public class RateCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Command(name = "add", description = "Добавить оценку поездки")
    public void addRating(
            @CommandLine.Option(names = "--trip-id", required = true) int tripId,
            @CommandLine.Option(names = "--user-id", required = true) int userId,
            @CommandLine.Option(names = "--rating", required = true, description = "Rating (1-5)") int rating,
            @CommandLine.Option(names = "--comment", description = "Comment") String comment
    ) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            provider.createRating(new Rating(0, rating, comment, new java.sql.Date(System.currentTimeMillis())), tripId);
            System.out.println("Оценка добавлена успешно.");
        } catch (SQLException e) {
            System.err.println("Ошибка добавления оценки: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Используйте `rate --help` для просмотра доступных команд.");
    }
}

