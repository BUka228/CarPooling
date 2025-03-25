package dao.triipdao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.csv.CsvTripDao;
import org.junit.jupiter.api.io.TempDir;

public class CsvTripDaoTest extends AbstractTripDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected TripDao createTripDao() {
        File tempFile = tempDir.resolve("test-trips.csv").toFile();
        return new CsvTripDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}