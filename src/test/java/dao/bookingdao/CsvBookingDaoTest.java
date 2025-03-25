package dao.bookingdao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.csv.CsvBookingDao;
import org.junit.jupiter.api.io.TempDir;

public class CsvBookingDaoTest extends AbstractBookingDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected BookingDao createBookingDao() {
        File tempFile = tempDir.resolve("test-bookings.csv").toFile();
        return new CsvBookingDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}
