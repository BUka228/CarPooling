package providers;

import converters.CsvConverter;
import converters.GenericConverter;
import model.HistoryContentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvDataProviderTest {
    private static final String FILE_PATH = "test.csv";
    private static final Logger log = LoggerFactory.getLogger(CsvDataProviderTest.class);
    private CsvDataProvider<HistoryContentTest> dataProvider;
    private GenericConverter<HistoryContentTest, String[]> mockConverter;

    @BeforeEach
    void setUp() {
        mockConverter = mock(GenericConverter.class);
        dataProvider = new CsvDataProvider<>(FILE_PATH, mockConverter);
    }

    @Test
    void testSaveRecord_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");
        String[] recordData = {"1", "user", "CREATE", "Sample Content"};

        // Мокаем сериализацию
        try {
            when(mockConverter.serialize(content)).thenReturn(recordData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        assertDoesNotThrow(() -> dataProvider.saveRecord(content));

        // Проверка, что файл был создан
        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "CSV файл не существует");

        // Очистка файла после теста
        //file.delete();
    }

    @Test
    void testGetRecordById_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        // Мокаем десериализацию
        when(mockConverter.getId(content)).thenReturn("1");
        try {
            when(mockConverter.deserialize(new String[]{"1", "user", "CREATE", "Sample Content"})).thenReturn(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        assertDoesNotThrow(() -> {
            HistoryContentTest result = dataProvider.getRecordById("1");
            assertNotNull(result, "Результат не должен быть null");
            assertEquals("1", result.getId(), "ID не совпадает");
        });
    }

    @Test
    void testGetAllRecords_Success() {
        HistoryContentTest content1 = new HistoryContentTest("1", "user", "CREATE", "Sample Content");
        HistoryContentTest content2 = new HistoryContentTest("2", "user", "UPDATE", "Another Content");

        try {
            when(mockConverter.deserialize(new String[]{"1", "user", "CREATE", "Sample Content"})).thenReturn(content1);
            when(mockConverter.deserialize(new String[]{"2", "user", "UPDATE", "Another Content"})).thenReturn(content2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /*CsvConverter<HistoryContentTest> converter = new CsvConverter<>(HistoryContentTest.class);
        CsvDataProvider<HistoryContentTest> dataProvider2 = new CsvDataProvider<>(FILE_PATH, converter);
        dataProvider2.saveRecord(content1);
        dataProvider2.saveRecord(content2);
        log.info(dataProvider2.getAllRecords().toString());*/




        List<HistoryContentTest> records = dataProvider.getAllRecords();
        assertNotNull(records, "Список записей не должен быть null");
        assertEquals(2, records.size(), "Количество записей не совпадает");
    }

    @Test
    void testInitDataSource_FileNotExist() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        assertDoesNotThrow(() -> dataProvider.initDataSource());
        assertTrue(file.exists(), "CSV файл не был создан");

        // Очистка файла после теста
        file.delete();
    }

    @Test
    void testInitDataSource_FileExists() {
        File file = new File(FILE_PATH);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() -> dataProvider.initDataSource());
        assertTrue(file.exists(), "CSV файл должен существовать");

        // Очистка файла после теста
        file.delete();
    }
}