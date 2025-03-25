package dao.userdao;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.csv.CsvTripDao;
import com.carpooling.dao.csv.CsvUserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class CsvUserDaoTest extends AbstractUserDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected UserDao createUserDao() {
        File tempFile = tempDir.resolve("test-trips.csv").toFile();
        return new CsvUserDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {}
}