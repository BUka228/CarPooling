package cli;

import com.man.Main;
import model.database.Booking;
import picocli.CommandLine;
import providers.JdbcDataProvider;

import java.sql.SQLException;

@CommandLine.Command(name = "book", description = "Команды для управления бронированием")
public class BookingCommands implements Runnable {

    @CommandLine.ParentCommand
    private Main main;

    @CommandLine.Command(name = "single", description = "Бронирование одиночного места")
    public void bookSingleSeat(
            @CommandLine.Option(names = "--trip-id", required = true) int tripId,
            @CommandLine.Option(names = "--user-id", required = true) int userId,
            @CommandLine.Option(names = "--passport", required = true) String passport,
            @CommandLine.Option(names = "--expiry", required = true) String expiry
    ) {
        try {
            JdbcDataProvider provider = main.getDataProvider();
            provider.createBooking(new Booking(0, (byte) 1, "CONFIRMED", java.sql.Date.valueOf(java.time.LocalDate.now()), passport, java.sql.Date.valueOf(expiry)), tripId, userId);
            System.out.println("Бронирование совершено.");
        } catch (SQLException e) {
            System.err.println("Ошибка бронирования места: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Используйте `book --help` для просмотра доступных команд.");
    }
}

