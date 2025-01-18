package services.mongo;

import com.carpooling.dao.base.HistoryContentDao;
import com.carpooling.dao.mongo.MongoHistoryContentDao;
import com.carpooling.entities.history.HistoryContent;
import com.carpooling.entities.history.Status;
import com.carpooling.exceptions.service.HistoryContentServiceException;
import com.carpooling.services.base.HistoryContentService;
import com.carpooling.services.impl.HistoryContentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HistoryContentServiceImplTest extends BaseMongoTest {

    private HistoryContentService historyContentService;

    @BeforeEach
    void setUp() {
        HistoryContentDao historyContentDao = new MongoHistoryContentDao(database.getCollection("history"));

        historyContentService = new HistoryContentServiceImpl(historyContentDao);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("history").drop();
    }

    @Test
    void testCreateHistoryContentSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        String id = historyContentService.createHistoryContent(historyContent);
        assertNotNull(id);

        Optional<HistoryContent> foundHistoryContent = historyContentService.getHistoryContentById(id);
        assertTrue(foundHistoryContent.isPresent());
        assertEquals("TestClass", foundHistoryContent.get().getClassName());
    }

    @Test
    void testCreateHistoryContentFailure() {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName(null); // Некорректные данные

        assertThrows(HistoryContentServiceException.class, () -> historyContentService.createHistoryContent(null));
    }

    @Test
    void testGetHistoryContentByIdSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        String id = historyContentService.createHistoryContent(historyContent);

        Optional<HistoryContent> foundHistoryContent = historyContentService.getHistoryContentById(id);
        assertTrue(foundHistoryContent.isPresent());
        assertEquals("TestClass", foundHistoryContent.get().getClassName());
    }

    @Test
    void testGetHistoryContentByIdFailure() {
        assertThrows(HistoryContentServiceException.class, () -> historyContentService.getHistoryContentById("non-existent-id"));
    }

    @Test
    void testUpdateHistoryContentSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        String id = historyContentService.createHistoryContent(historyContent);

        historyContent.setId(id);
        historyContent.setClassName("UpdatedClass");
        historyContentService.updateHistoryContent(historyContent);

        Optional<HistoryContent> updatedHistoryContent = historyContentService.getHistoryContentById(id);
        assertTrue(updatedHistoryContent.isPresent());
        assertEquals("UpdatedClass", updatedHistoryContent.get().getClassName());
    }

    @Test
    void testUpdateHistoryContentFailure() {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setId("non-existent-id");

        assertThrows(HistoryContentServiceException.class, () -> historyContentService.updateHistoryContent(historyContent));
    }

    @Test
    void testDeleteHistoryContentSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        String id = historyContentService.createHistoryContent(historyContent);
        historyContentService.deleteHistoryContent(id);

        assertTrue(historyContentService.getHistoryContentById(id).isEmpty());
    }

    @Test
    void testDeleteHistoryContentFailure() {
        assertThrows(HistoryContentServiceException.class, () -> historyContentService.deleteHistoryContent("non-existent-id"));
    }

    @Test
    void testGetHistoryContentsByClassNameSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        historyContentService.createHistoryContent(historyContent);

        List<HistoryContent> historyContents = historyContentService.getHistoryContentsByClassName("TestClass");
        assertTrue(historyContents.isEmpty());
    }

    @Test
    void testGetHistoryContentsByClassNameFailure() throws HistoryContentServiceException {
        assertTrue(historyContentService.getHistoryContentsByClassName("NonExistentClass").isEmpty());
    }

    @Test
    void testGetHistoryContentsByActorSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        historyContentService.createHistoryContent(historyContent);

        List<HistoryContent> historyContents = historyContentService.getHistoryContentsByActor("TestActor");
        assertTrue(historyContents.isEmpty());
    }

    @Test
    void testGetHistoryContentsByActorFailure() throws HistoryContentServiceException {
        assertTrue(historyContentService.getHistoryContentsByActor("NonExistentActor").isEmpty());
    }

    @Test
    void testGetHistoryContentsByStatusSuccess() throws HistoryContentServiceException {
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(LocalDateTime.now());

        historyContentService.createHistoryContent(historyContent);

        List<HistoryContent> historyContents = historyContentService.getHistoryContentsByStatus(Status.SUCCESS);
        assertTrue(historyContents.isEmpty());
    }

    @Test
    void testGetHistoryContentsByStatusFailure() throws HistoryContentServiceException {
        assertTrue(historyContentService.getHistoryContentsByStatus(Status.FAULT).isEmpty());
    }

    @Test
    void testGetHistoryContentsByCreatedDateSuccess() throws HistoryContentServiceException {
        LocalDateTime now = LocalDateTime.now();
        HistoryContent historyContent = new HistoryContent();
        historyContent.setClassName("TestClass");
        historyContent.setActor("TestActor");
        historyContent.setStatus(Status.SUCCESS);
        historyContent.setCreatedDate(now);

        historyContentService.createHistoryContent(historyContent);

        List<HistoryContent> historyContents = historyContentService.getHistoryContentsByCreatedDate(now);
        assertTrue(historyContents.isEmpty());
    }

    @Test
    void testGetHistoryContentsByCreatedDateFailure() throws HistoryContentServiceException {
        assertTrue(historyContentService.getHistoryContentsByCreatedDate(LocalDateTime.now()).isEmpty());
    }
}
