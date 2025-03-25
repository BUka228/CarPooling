package dao.routedao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.csv.CsvRouteDao;
import org.junit.jupiter.api.io.TempDir;

public class CsvRouteDaoTest extends AbstractRouteDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected RouteDao createRouteDao() {
        File tempFile = tempDir.resolve("test-routes.csv").toFile();
        return new CsvRouteDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}