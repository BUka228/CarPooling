package dao.userdao;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.xml.XmlUserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class XmlUserDaoTest extends AbstractUserDaoTest {

    @TempDir
    Path tempDir;


    @Override
    protected UserDao createUserDao() {
        File tempFile = tempDir.resolve("test-users.csv").toFile();
        return new XmlUserDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {}
}