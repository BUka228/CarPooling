package providers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.HistoryContentTest;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class XmlDataProviderTest {

    private static final String TEMP_FILE_PREFIX = "xml_data_provider_test";
    private Path tempFile;
    private XmlDataProvider<HistoryContentTest> dataProvider;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем временный файл
        //tempFile = Files.createFile(Paths.get(TEMP_FILE_PREFIX + ".xml"));
        tempFile = Files.createTempFile(TEMP_FILE_PREFIX, ".xml");
        dataProvider = new XmlDataProvider<>(HistoryContentTest.class);
        dataProvider.initDataSource(tempFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаляем временный файл
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testSaveRecordAndGetAllRecords() {
        HistoryContentTest record1 = new HistoryContentTest("1", "Alice", "create", "Test content 1");
        HistoryContentTest record2 = new HistoryContentTest("2", "Bob", "update", "Test content 2");

        dataProvider.saveRecord(record1);
        dataProvider.saveRecord(record2);

        List<HistoryContentTest> records = dataProvider.getAllRecords();
        assertEquals(2, records.size());
        assertTrue(records.contains(record1));
        assertTrue(records.contains(record2));
    }

    @Test
    void testGetRecordById() {
        HistoryContentTest record = new HistoryContentTest("1", "Alice", "create", "Test content");

        dataProvider.saveRecord(record);

        HistoryContentTest fetchedRecord = dataProvider.getRecordById(1L);
        assertNotNull(fetchedRecord);
        assertEquals("Alice", fetchedRecord.getActor());
        assertEquals("create", fetchedRecord.getAction());
        assertEquals("Test content", fetchedRecord.getContent());
    }

    @Test
    void testDeleteRecord() {
        HistoryContentTest record1 = new HistoryContentTest("1", "Alice", "create", "Test content 1");
        HistoryContentTest record2 = new HistoryContentTest("2", "Bob", "update", "Test content 2");

        dataProvider.saveRecord(record1);
        dataProvider.saveRecord(record2);

        dataProvider.deleteRecord(1L);

        List<HistoryContentTest> records = dataProvider.getAllRecords();
        assertEquals(1, records.size());
        assertFalse(records.contains(record1));
        assertTrue(records.contains(record2));
    }

    @Test
    void testGetAllRecordsWhenFileIsEmpty() {
        List<HistoryContentTest> records = dataProvider.getAllRecords();
        assertTrue(records.isEmpty());
    }


    @Test
    void testInitDataSourceCreatesNewFile() throws IOException {
        Path newFile = Files.createTempFile("new_test_file", ".xml");
        Files.deleteIfExists(newFile); // Удаляем файл, чтобы проверить создание

        XmlDataProvider<HistoryContentTest> newDataProvider = new XmlDataProvider<>(HistoryContentTest.class);
        newDataProvider.initDataSource(newFile.toString());

        assertTrue(Files.exists(newFile));
        Files.deleteIfExists(newFile);
    }
}

