package dao.routedao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.xml.XmlRouteDao;
import org.junit.jupiter.api.io.TempDir;

public class XmlRouteDaoTest extends AbstractRouteDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected RouteDao createRouteDao() {
        File tempFile = tempDir.resolve("test-routes.xml").toFile();
        return new XmlRouteDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}