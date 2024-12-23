package cli;




import com.man.Main;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@CommandLine.Command(name = "search", description = "Поиск доступных поездок")
public class SearchCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Option(names = "--from", description = "Место отправления", required = true)
    private String from;

    @CommandLine.Option(names = "--to", description = "Место назначения", required = true)
    private String to;

    @CommandLine.Option(names = "--date", description = "Дата поездки", required = true)
    private String date;

    @Override
    public void run() {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            //var trips = provider.searchTrips(from, to, java.sql.Date.valueOf(date));
            var trips = List.of(Objects.requireNonNull(provider.getTripById(0)));
            trips.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("Error searching trips: " + e.getMessage());
            System.err.println("Ошибка поиска поездок: " + e.getMessage());
        }
    }
}
