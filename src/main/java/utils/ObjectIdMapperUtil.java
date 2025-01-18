package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class ObjectIdMapperUtil {

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Модуль для кастомной десериализации ObjectId в String
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new ObjectIdToStringDeserializer());

        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static class ObjectIdToStringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);

            // Если ObjectId представлен как объект с полем "$oid"
            if (node.isObject() && node.has("$oid")) {
                return node.get("$oid").asText(); // Возвращаем строку из поля "$oid"
            }

            // Если ObjectId представлен как строка
            if (node.isTextual()) {
                return node.asText(); // Возвращаем строку как есть
            }

            // Если формат не поддерживается
            throw new IllegalArgumentException("Invalid ObjectId format: " + node);
        }
    }
}