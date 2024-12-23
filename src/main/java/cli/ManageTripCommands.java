package cli;

import com.man.Main;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "manage-trip", description = "Управление поездками")
public class ManageTripCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Command(name = "edit", description = "Редактировать существующую поездку")
    public void editTrip(
            @CommandLine.Option(names = "--trip-id", required = true) int tripId,
            @CommandLine.Option(names = "--departure", description = "Новое время отправления") String departure,
            @CommandLine.Option(names = "--max-passengers", description = "Новое максимальное количество пассажиров") Byte maxPassengers,
            @CommandLine.Option(names = "--status", description = "Новый статус") String status
    ) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            var trip = provider.getTripById(tripId);
            if (trip == null) {
                System.err.println("Поездка не найдена.");
                return;
            }
            if (departure != null) trip.setDepartureTime(java.sql.Timestamp.valueOf(departure));
            if (maxPassengers != null) trip.setMaxPassengers(maxPassengers);
            if (status != null) trip.setStatus(status);

            provider.updateTrip(trip, 0);
            System.out.println("Поездка успешно обновлена.");
        } catch (SQLException e) {
            System.err.println("Ошибка редактирования поездки: " + e.getMessage());
        }
    }

    @CommandLine.Command(name = "delete", description = "Удалить существующую поездку")
    public void deleteTrip(@CommandLine.Option(names = "--trip-id", required = true) int tripId) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            provider.deleteTrip(tripId);
            System.out.println("Поездка успешно удалена.");
        } catch (SQLException e) {
            System.err.println("Ошибка удаления поездки: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Используйте `manage-trip --help` для просмотра доступных команд.");
    }
}

