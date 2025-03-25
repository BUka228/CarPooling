package dao.bookingdao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.xml.XmlBookingDao;
import org.junit.jupiter.api.io.TempDir;

public class XmlBookingDaoTest extends AbstractBookingDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected BookingDao createBookingDao() {
        File tempFile = tempDir.resolve("test-bookings.xml").toFile();
        return new XmlBookingDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}