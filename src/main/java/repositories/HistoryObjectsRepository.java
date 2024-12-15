package repositories;

import com.mongodb.client.MongoCollection;
import converters.MongoConverter;
import exceptions.RepositoryException;
import model.HistoryContent;
import org.bson.Document;
import providers.IDataProvider;
import providers.MongoDataProvider;

import java.util.List;

public class HistoryObjectsRepository extends GenericRepository<HistoryContent> {
    public HistoryObjectsRepository(IDataProvider<HistoryContent> provider) {
        super(provider);
    }

    // Фабричный метод для создания репозитория с настройками по умолчанию
    public static HistoryObjectsRepository defaultMongoRepository(MongoCollection<Document> collection) {
        MongoConverter<HistoryContent> converter = new MongoConverter<>(HistoryContent.class);
        MongoDataProvider<HistoryContent> provider = new MongoDataProvider<>(collection, converter);
        return new HistoryObjectsRepository(provider);
    }

    public List<HistoryContent> findByActor(String actor) {
        try {
            return findAll().stream()
                    .filter(history -> actor.equals(history.getActor()))
                    .toList();
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при поиске историй по actor: " + actor, e);
        }
    }

}
