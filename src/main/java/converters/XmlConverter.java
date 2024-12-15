package converters;

import exceptions.ConverterException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringReader;
import java.io.StringWriter;

public class XmlConverter<T> implements GenericConverter<T, String> {
    private final Serializer serializer;
    private final Class<T> type;

    public XmlConverter(Class<T> type) {
        this.serializer = new Persister();
        this.type = type;
    }

    @Override
    public String serialize(T entity) {
        try {
            StringWriter writer = new StringWriter();
            serializer.write(entity, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new ConverterException("Ошибка при сериализации объекта в XML", e);
        }
    }

    @Override
    public T deserialize(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new ConverterException("Ошибка при десериализации XML в объект: пустой или null XML");
        }
        try {
            return serializer.read(type, new StringReader(xml));
        } catch (Exception e) {
            throw new ConverterException("Ошибка при десериализации XML в объект", e);
        }
    }

    @Override
    public String getId(T entity) {
        try {
            // Предполагается, что объект имеет поле id
            return String.valueOf(entity.hashCode()); // Замените на реальную логику
        } catch (Exception e) {
            throw new ConverterException("Ошибка при получении ID из объекта", e);
        }
    }
}
