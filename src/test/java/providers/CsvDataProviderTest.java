package providers;

import static org.junit.jupiter.api.Assertions.*;

import model.HistoryContentTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class CsvDataProviderTest {

    private static Path tempFile;
    private CsvDataProvider<HistoryContentTest> dataProvider;

    @BeforeAll
    static void setUpBeforeClass() throws IOException {
        // Создаем временный файл перед всеми тестами
        //tempFile = Files.createFile(Paths.get("temp.csv"));
        tempFile = Files.createTempFile("temp", ".csv");
    }

    @AfterAll
    static void tearDownAfterClass() throws IOException {
        // Удаляем временный файл после всех тестов
        Files.deleteIfExists(tempFile);
    }

    @BeforeEach
    void setUp() {
        // Создаем новый экземпляр CsvDataProvider для каждого теста
        dataProvider = new CsvDataProvider<>(HistoryContentTest.class);
        dataProvider.initDataSource(tempFile.toString());
    }

    @Test
    void testInitDataSource_FileExists() {
        // Проверяем, что при инициализации источник данных создается корректно
        assertDoesNotThrow(() -> dataProvider.initDataSource(tempFile.toString()));
    }

    @Test
    void testSaveRecord() {
        HistoryContentTest record = new HistoryContentTest();
        record.setId("1");
        record.setActor("actor1");
        record.setAction("action1");
        record.setContent("content1");

        List<HistoryContentTest> records1 = dataProvider.getAllRecords();

        // Сохраняем запись
        assertDoesNotThrow(() -> dataProvider.saveRecord(record));

        // Проверяем, что запись сохранена
        List<HistoryContentTest> records2 = dataProvider.getAllRecords();
        HistoryContentTest savedRecord = records2.get(records2.size() - 1);
        assertEquals(records1.size() + 1, records2.size());
        assertEquals(record, savedRecord);
    }

    @Test
    void testDeleteRecord() {
        HistoryContentTest record = new HistoryContentTest();
        record.setId("1");
        record.setActor("actor1");
        record.setAction("action1");
        record.setContent("content1");

        dataProvider.saveRecord(record);
        List<HistoryContentTest> records1 = dataProvider.getAllRecords();


        // Удаляем запись
        assertDoesNotThrow(() -> dataProvider.deleteRecord(1));

        // Проверяем, что запись удалена
        List<HistoryContentTest> records2 = dataProvider.getAllRecords();
        assertEquals(records1.size() - 1, records2.size());
    }

    @Test
    void testGetRecordById() {
        HistoryContentTest record = new HistoryContentTest();
        record.setId("1");
        record.setActor("actor1");
        record.setAction("action1");
        record.setContent("content1");

        dataProvider.saveRecord(record);

        // Получаем запись по ID
        HistoryContentTest retrievedRecord = dataProvider.getRecordById(1);
        assertNotNull(retrievedRecord);
        assertEquals(record, retrievedRecord);
    }

    @Test
    void testGetAllRecords() {
        HistoryContentTest record1 = new HistoryContentTest();
        record1.setId("1");
        record1.setActor("actor1");
        record1.setAction("action1");
        record1.setContent("content1");

        HistoryContentTest record2 = new HistoryContentTest();
        record2.setId("2");
        record2.setActor("actor2");
        record2.setAction("action2");
        record2.setContent("content2");

        dataProvider.saveRecord(record1);
        dataProvider.saveRecord(record2);

        // Проверяем, что все записи получены корректно
        List<HistoryContentTest> records = dataProvider.getAllRecords();
        assertEquals(2, records.size());
        assertTrue(records.contains(record1));
        assertTrue(records.contains(record2));
    }
}
