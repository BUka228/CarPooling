package converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ConverterException;
import org.bson.Document;

public class MongoConverter<T> implements GenericConverter<T, Document> {
    private final Class<T> type;
    private final ObjectMapper objectMapper;

    public MongoConverter(Class<T> type) {
        this.type = type;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Document serialize(T entity) {
        try {
            String json = objectMapper.writeValueAsString(entity);
            return Document.parse(json);
        } catch (Exception e) {
            throw new ConverterException("Ошибка при сериализации объекта в MongoDB Document", e);
        }
    }

    @Override
    public T deserialize(Document document) {
        // Проверяем, что Document не пустой
        if (document == null || document.isEmpty()) {
            throw new ConverterException("Document пуст и не может быть десериализован");
        }
        try {

            String json = document.toJson();
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new ConverterException("Ошибка при десериализации MongoDB Document в объект", e);
        }
    }

    @Override
    public String getId(T entity) {
        try {
            JsonNode jsonNode = objectMapper.convertValue(entity, JsonNode.class);
            JsonNode idNode = jsonNode.get("id");

            if (idNode == null || idNode.isNull()) {
                throw new ConverterException("Поле 'id' отсутствует или равно null");
            }

            return idNode.asText();
        } catch (Exception e) {
            throw new ConverterException("Ошибка при получении ID объекта", e);
        }
    }
}
