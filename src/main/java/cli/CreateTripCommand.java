package cli;

import com.man.Main;
import model.database.Trip;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "create-trip", description = "Создать новую поездку")
public class CreateTripCommand implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Option(names = "--user-id", required = true, description = "ID пользователя, создающего поездку")
    private int userId;

    @CommandLine.Option(names = "--route-id", required = true, description = "ID маршрута для поездки")
    private int routeId;

    @CommandLine.Option(names = "--departure", required = true, description = "Время отправления (формат: yyyy-MM-dd HH:mm:ss)")
    private String departure;

    @CommandLine.Option(names = "--max-passengers", required = true, description = "Максимальное количество пассажиров")
    private byte maxPassengers;

    @CommandLine.Option(names = "--status", description = "Начальный статус поездки", defaultValue = "available")
    private String status;

    @Override
    public void run() {
        try {
            JdbcDataProvider provider = main.getDataProvider();

            // Создаем новую поездку
            Trip trip = new Trip(
                    0,
                    java.sql.Timestamp.valueOf(departure),
                    maxPassengers,
                    new java.sql.Timestamp(System.currentTimeMillis()), // дата создания
                    status,
                    true // редактируемость
            );

            // Сохраняем поездку
            provider.createTrip(trip, userId, routeId);
            System.out.println("Поездка успешно создана.");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании поездки: " + e.getMessage());
        }
    }
}
