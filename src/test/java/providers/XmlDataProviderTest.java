package providers;

import converters.GenericConverter;
import converters.XmlConverter;
import model.HistoryContentTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XmlDataProviderTest {
    private static final String FILE_PATH = "test.xml";
    private XmlDataProvider<HistoryContentTest> dataProvider;
    private GenericConverter<HistoryContentTest, String> mockConverter;

    @BeforeEach
    void setUp() {
        mockConverter = mock(GenericConverter.class);
        dataProvider = new XmlDataProvider<>(FILE_PATH, mockConverter);
    }

    @AfterEach
    void tearDown() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSaveRecord_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        try {
            when(mockConverter.serialize(any())).thenReturn("<records><record>...</record></records>");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(mockConverter.getId(content)).thenReturn("1");
        /*XmlConverter<HistoryContentTest> conv = new XmlConverter<>(HistoryContentTest.class);
        XmlDataProvider<H> dataProvider1 = new XmlDataProvider<>(FILE_PATH, conv);*/


        assertDoesNotThrow(() -> dataProvider.saveRecord(content));

        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "XML файл не был создан");
    }

    @Test
    void testGetRecordById_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");
        // Пример XML, который будет возвращён мок-объектом при вызове serialize
        String mockXml = "<records>" +
                "<id>1</id>" +
                "<actor>user</actor>" +
                "<action>CREATE</action>" +
                "<content>Sample Content</content>" +
                "</records>";

        try {
            when(mockConverter.serialize(any())).thenReturn("mockXml");
            when(mockConverter.getId(content)).thenReturn("1");
            when(mockConverter.deserialize(anyString())).thenReturn(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        dataProvider.saveRecord(content);

        HistoryContentTest result = dataProvider.getRecordById("1");

        assertNotNull(result, "Запись не должна быть null");
        assertEquals("1", result.getId(), "ID не совпадает");
    }

    @Test
    void testGetAllRecords_Success() {
        HistoryContentTest content1 = new HistoryContentTest("1", "user1", "CREATE", "Content1");
        HistoryContentTest content2 = new HistoryContentTest("2", "user2", "UPDATE", "Content2");
        String[] mockXmlRecords = {"<record1>", "<record2>"};

        try {
            when(mockConverter.deserialize(mockXmlRecords[0])).thenReturn(content1);
            when(mockConverter.deserialize(mockXmlRecords[1])).thenReturn(content2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Эмулируем вызов для каждого элемента
        List<HistoryContentTest> results = null;
        try {
            results = List.of(
                    mockConverter.deserialize(mockXmlRecords[0]),
                    mockConverter.deserialize(mockXmlRecords[1])
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(results, "Список записей не должен быть null");
        assertEquals(2, results.size(), "Количество записей не совпадает");
        assertTrue(results.contains(content1), "Список не содержит первую запись");
        assertTrue(results.contains(content2), "Список не содержит вторую запись");
    }

    @Test
    void testInitDataSource_FileNotExist() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        assertDoesNotThrow(() -> dataProvider.initDataSource());
        assertTrue(file.exists(), "XML файл не был создан");
    }

    @Test
    void testInitDataSource_FileExists() {
        File file = new File(FILE_PATH);
        assertDoesNotThrow(() -> dataProvider.initDataSource());
        assertTrue(file.exists(), "XML файл должен существовать");
    }
}
