package dao.ratingdao;

import java.nio.file.Path;
import java.io.File;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.xml.XmlRatingDao;
import org.junit.jupiter.api.io.TempDir;

public class XmlRatingDaoTest extends AbstractRatingDaoTest {

    @TempDir
    Path tempDir;

    @Override
    protected RatingDao createRatingDao() {
        File tempFile = tempDir.resolve("test-ratings.xml").toFile();
        return new XmlRatingDao(tempFile.getAbsolutePath());
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется, так как @TempDir автоматически удаляет временные файлы
    }
}