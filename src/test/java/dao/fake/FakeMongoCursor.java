package dao.fake;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

public class FakeMongoCursor implements MongoCursor<Document> {

    private final Document document;
    private boolean hasNext = true;

    public FakeMongoCursor(Document document) {
        this.document = document;
    }

    @Override
    public void close() {
        // Ничего не делаем
    }

    @Override
    public boolean hasNext() {
        boolean result = hasNext;
        hasNext = false; // После первого вызова возвращаем false
        return result;
    }

    @Override
    public Document next() {
        return document;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public Document tryNext() {
        return hasNext() ? next() : null;
    }

    @Override
    public ServerCursor getServerCursor() {
        return null; // Возвращаем null, так как это фейковый курсор
    }

    @Override
    public ServerAddress getServerAddress() {
        return new ServerAddress("localhost", 27017); // Возвращаем фейковый адрес сервера
    }
}