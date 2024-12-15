package converters;

import exceptions.ConverterException;
import model.HistoryContentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class XmlConverterTest {
    private static final Logger log = LoggerFactory.getLogger(XmlConverterTest.class);
    private XmlConverter<HistoryContentTest> converter;

    @BeforeEach
    void setUp() {
        converter = new XmlConverter<>(HistoryContentTest.class);
    }

    @Test
    void testSerialize_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        String xml = converter.serialize(content);

        assertNotNull(xml, "Сериализация вернула null");
        assertTrue(xml.contains("<historyContentTest>"), "XML не содержит ожидаемого корневого тега");
        assertTrue(xml.contains("<id>1</id>"), "XML не содержит ID");
        assertTrue(xml.contains("<actor>user</actor>"), "XML не содержит Actor");
        assertTrue(xml.contains("<action>CREATE</action>"), "XML не содержит Action");
        assertTrue(xml.contains("<content>Sample Content</content>"), "XML не содержит Content");
    }

    @Test
    void testDeserialize_Success() {
        String xml = """
            <HistoryContent>
                <id>1</id>
                <actor>user</actor>
                <action>CREATE</action>
                <content>Sample Content</content>
            </HistoryContent>
        """;

        HistoryContentTest content = converter.deserialize(xml);

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

        assertEquals("Ошибка при сериализации объекта в XML", exception.getMessage(), "Сообщение исключения неверно");
    }


    @Test
    void testDeserialize_NullXml() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.deserialize(null));

        assertEquals("Ошибка при десериализации XML в объект: пустой или null XML  ", exception.getMessage(), "Сообщение исключения неверно");
    }

    @Test
    void testGetId_NullEntity() {
        Exception exception = assertThrows(ConverterException.class, () -> converter.getId(null));

        assertEquals("Ошибка при получении ID из объекта", exception.getMessage(), "Сообщение исключения неверно");
    }
}
