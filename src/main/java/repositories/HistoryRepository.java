package repositories;

import com.man.Constants;
import com.mongodb.client.MongoCollection;
import model.HistoryContent;
import org.bson.Document;
import utils.ConfigurationUtil;

import java.io.IOException;

import static utils.MongoDBUtil.getCollection;

public class HistoryRepository extends MongoDBRepository<HistoryContent> {
    public HistoryRepository(MongoCollection<Document> collection)  {
        super(HistoryContent.class, collection);
    }
}
