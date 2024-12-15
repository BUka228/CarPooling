package converters;

import exceptions.ConverterException;
import man.TestGener;
import model.HistoryContentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvConverterTest extends TestGener {

    private CsvConverter<HistoryContentTest> converter;

    @BeforeEach
    void setUp() {
        converter = new CsvConverter<>(HistoryContentTest.class);
    }

    @Test
    void testSerialize_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        String[] csvData = converter.serialize(content);

        assertNotNull(csvData, "Сериализация вернула null");
        assertEquals(4, csvData.length, "Количество колонок в CSV не совпадает");
        assertEquals("1", csvData[0], "Поле ID не совпадает");
        assertEquals("user", csvData[1], "Поле Actor не совпадает");
        assertEquals("CREATE", csvData[2], "Поле Action не совпадает");
        assertEquals("Sample Content", csvData[3], "Поле Content не совпадает");

    }

    @Test
    void testDeserialize_Success() {
        String[] csvData = {"1", "user", "CREATE", "Sample Content"};

        HistoryContentTest content = converter.deserialize(csvData);

        assertNotNull(content, "Десериализация вернула null");
        assertEquals("1", content.getId(), "Поле ID не совпадает");
        assertEquals("user", content.getActor(), "Поле Actor не совпадает");
        assertEquals("CREATE", content.getAction(), "Поле Action не совпадает");
        assertEquals("Sample Content", content.getContent(), "Поле Content не совпадает");
    }

    @Test
    void testGetId_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        String id = converter.getId(content);

        assertNotNull(id, "ID не должен быть null");
        assertEquals(String.valueOf(content.hashCode()), id, "ID не совпадает");
    }

    @Test
    void testSerialize_NullEntity() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.serialize(null));

        assertEquals("Ошибка при сериализации объекта в CSV: объект равен null", exception.getMessage(), "Сообщение исключения неверно");
    }

    @Test
    void testDeserialize_InvalidData() {
        String[] invalidData = {};

        Exception exception = assertThrows(ConverterException.class, () -> converter.deserialize(invalidData));

        assertEquals("Не удалось десериализовать CSV: пустые данные", exception.getMessage(), "Сообщение исключения неверно");
    }



    @Test
    void testGetId_NullEntity() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.getId(null));

        assertEquals("Ошибка при получении ID из объекта", exception.getMessage(), "Сообщение исключения неверно");
    }
}
