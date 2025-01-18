package data.dao.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import utils.ObjectIdMapperUtil;

import static com.man.constant.Constants.MONGO_ID;
import static com.man.constant.ErrorMessages.*;


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
            Document document = Document.parse(json);
            document.remove("id");
            return document;
        } catch (JsonProcessingException e) {
            log.error(ERROR_CONVERT_OBJECT_TO_DOCUMENT, object, e);
            throw new IllegalStateException(CONVERT_OBJECT_TO_DOCUMENT_ERROR, e);
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
            // Убираем _id, заменяем его на id
            if (document.containsKey(MONGO_ID)) {
                document.put("id", document.getObjectId(MONGO_ID).toHexString());
                document.remove(MONGO_ID);
            }
            String json = document.toJson();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(ERROR_CONVERT_DOCUMENT_TO_OBJECT, document, e);
            throw new IllegalStateException(CONVERT_DOCUMENT_TO_OBJECT_ERROR, e);
        }
    }
}
