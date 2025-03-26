package com.carpooling.dao.mongo;

import com.carpooling.utils.ObjectIdMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import static com.carpooling.constants.Constants.MONGO_ID;
import static com.carpooling.constants.ErrorMessages.*;


@Slf4j
public abstract class AbstractMongoDao<T> {

    protected final MongoCollection<Document> collection;
    private final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public AbstractMongoDao(MongoCollection<Document> collection, Class<T> clazz) {
        this.collection = collection;
        this.clazz = clazz;
        objectMapper = ObjectIdMapperUtil.createObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Преобразует объект Java в MongoDB Document.
     *
     * @param object Объект для преобразования.
     * @return Документ MongoDB.
     */
    protected Document toDocument(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return Document.parse(json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to document: {}", object, e);
            throw new IllegalStateException("Error converting object to document", e);
        }
    }

    /**
     * Преобразует MongoDB Document в объект Java.
     *
     * @param document Документ MongoDB.
     * @return Объект Java.
     */
    protected T fromDocument(Document document) {
        try {
            if (document.containsKey(MONGO_ID)) {
                document.put("id", document.getObjectId(MONGO_ID).toHexString());
                document.remove(MONGO_ID);
            }
            return objectMapper.convertValue(document, clazz);
        } catch (Exception e) {
            log.error("Error converting document to object: {}", document, e);
            throw new IllegalStateException("Error converting document to object", e);
        }
    }
}
