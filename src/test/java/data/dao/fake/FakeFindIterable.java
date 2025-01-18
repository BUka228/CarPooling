package data.dao.fake;

import com.mongodb.CursorType;
import com.mongodb.ExplainVerbosity;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class FakeFindIterable implements FindIterable<Document> {

    private final Document document;

    public FakeFindIterable(Document document) {
        this.document = document;
    }

    @Override
    public MongoCursor<Document> iterator() {
        return new FakeMongoCursor(document);
    }

    @Override
    public MongoCursor<Document> cursor() {
        return null;
    }


    public FindIterable<Document> filter(Document filter) {
        return this;
    }

    @Override
    public FindIterable<Document> filter(Bson bson) {
        return null;
    }
    @Override
    public Document first() {
        return document; // Возвращаем документ
    }

    @Override
    public FindIterable<Document> limit(int limit) {
        return this;
    }

    @Override
    public FindIterable<Document> skip(int skip) {
        return this;
    }

    @Override
    public FindIterable<Document> maxTime(long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public FindIterable<Document> maxAwaitTime(long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public FindIterable<Document> projection(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> sort(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> noCursorTimeout(boolean b) {
        return null;
    }

    @Override
    public FindIterable<Document> oplogReplay(boolean b) {
        return null;
    }

    @Override
    public FindIterable<Document> partial(boolean b) {
        return null;
    }

    @Override
    public FindIterable<Document> cursorType(CursorType cursorType) {
        return null;
    }

    @Override
    public FindIterable<Document> batchSize(int i) {
        return null;
    }

    @Override
    public FindIterable<Document> collation(Collation collation) {
        return null;
    }

    @Override
    public FindIterable<Document> comment(String s) {
        return null;
    }

    @Override
    public FindIterable<Document> comment(BsonValue bsonValue) {
        return null;
    }

    @Override
    public FindIterable<Document> hint(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> hintString(String s) {
        return null;
    }

    @Override
    public FindIterable<Document> let(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> max(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> min(Bson bson) {
        return null;
    }

    @Override
    public FindIterable<Document> returnKey(boolean b) {
        return null;
    }

    @Override
    public FindIterable<Document> showRecordId(boolean b) {
        return null;
    }

    @Override
    public FindIterable<Document> allowDiskUse(Boolean aBoolean) {
        return null;
    }

    @Override
    public Document explain() {
        return null;
    }

    @Override
    public Document explain(ExplainVerbosity explainVerbosity) {
        return null;
    }

    @Override
    public <E> E explain(Class<E> aClass) {
        return null;
    }

    @Override
    public <E> E explain(Class<E> aClass, ExplainVerbosity explainVerbosity) {
        return null;
    }


    @Override
    public <U> MongoIterable<U> map(Function<Document, U> function) {
        return null;
    }

    @Override
    public <A extends Collection<? super Document>> A into(A objects) {
        return null;
    }

    // Остальные методы интерфейса FindIterable можно оставить пустыми или вернуть this
}