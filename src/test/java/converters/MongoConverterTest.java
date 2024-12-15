package converters;


import exceptions.ConverterException;
import model.HistoryContentTest;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MongoConverterTest {

    private MongoConverter<HistoryContentTest> converter;

    @BeforeEach
    void setUp() {
        converter = new MongoConverter<>(HistoryContentTest.class);
    }

    @Test
    void testSerialize_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        Document document = converter.serialize(content);

        assertNotNull(document, "Сериализация вернула null");
        assertEquals(document.getString("id"),"1" , "ID не совпадает");
        assertEquals(document.getString("actor"),"user" , "Actor не совпадает");
        assertEquals(document.getString("action"),"CREATE" , "Action не совпадает");
        assertEquals(document.getString("content"), "Sample Content", "Content не совпадает");
    }

    @Test
    void testDeserialize_Success() {
        Document document = new Document()
                .append("id", "1")
                .append("actor", "user")
                .append("action", "CREATE")
                .append("content", "Sample Content");

        HistoryContentTest content = converter.deserialize(document);

        assertNotNull(content, "Десериализация вернула null");
        assertEquals(content.getId(), "1" , "ID не совпадает");
        assertEquals(content.getActor(), "user" , "Actor не совпадает");
        assertEquals(content.getAction(), "CREATE" , "Action не совпадает");
        assertEquals(content.getContent(), "Sample Content", "Content не совпадает");
    }

    @Test
    void testGetId_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        String id = converter.getId(content);

        assertNotNull(id, "ID не должен быть null");
        assertEquals(id, "1" , "ID не совпадает");
    }

    @Test
    void testSerialize_NullEntity() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.serialize(null));

        assertEquals(exception.getMessage(), "Ошибка при сериализации объекта в MongoDB Document", "Сообщение исключения неверно");
    }

    @Test
    void testDeserialize_InvalidDocument() {
        Document invalidDocument = new Document();

        Exception exception = assertThrows(ConverterException.class, () -> converter.deserialize(invalidDocument));

        assertEquals(exception.getMessage(), "Ошибка при десериализации MongoDB Document в объект", "Сообщение исключения неверно");
    }

    @Test
    void testGetId_NullEntity() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.getId(null));

        assertEquals(exception.getMessage(), "Ошибка при получении ID объекта", "Сообщение исключения неверно" );
    }

    @Test
    void testGetId_InvalidStructure() {
        HistoryContentTest content = new HistoryContentTest(null, "user", "CREATE", "Sample Content");

        Exception exception = assertThrows(ConverterException.class, () -> converter.getId(content));

        assertEquals(exception.getMessage(), "Ошибка при получении ID объекта", "Сообщение исключения неверно");
    }
}
