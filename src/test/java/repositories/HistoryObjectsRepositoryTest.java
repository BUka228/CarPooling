package repositories;

import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;

public class HistoryObjectsRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(HistoryObjectsRepositoryTest.class);
    private MongoCollection<Document> collection;
    private HistoryObjectsRepository repository;

    /*@BeforeEach
    void setUp() {
        try {
            repository = HistoryObjectsRepository.defaultMongoRepository(MongoDBUtil.getCollection(
                    ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                    ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
            ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveAndFindRecord() {
        // Создаём объект для сохранения
        HistoryContent historyContent1 = new HistoryContent(
                "1",
                "testClass",
                LocalDateTime.now(),
                "system",
                "testMethod",
                Map.of("key1", "value1", "key2", 123),
                Status.SUCCESS
        );

        // Сохраняем объект
        assertDoesNotThrow(() -> repository.save(historyContent1));

        // Проверяем, что объект был сохранён
        HistoryContent foundContent = repository.findById("1");

        assertNotNull(foundContent, "Запись не должна быть null");
        assertEquals("1", foundContent.getId(), "ID не совпадает");
        assertEquals("system", foundContent.getActor(), "Actor не совпадает");
        assertEquals(Map.of("key1", "value1", "key2", 123), foundContent.getObject(), "Object не совпадает");
        assertEquals("testClass", foundContent.getClassName(), "ClassName не совпадает");

        assertDoesNotThrow(() -> repository.delete(historyContent1));
    }

    @Test
    void testFindByActor() {
        // Создаём объекты для сохранения
        HistoryContent content1 = new HistoryContent(
                "1",
                "testClass1",
                LocalDateTime.now(),
                "system",
                "testMethod",
                Map.of("key1", "value1", "key2", 123),
                Status.SUCCESS
        );
        HistoryContent content2 = new HistoryContent(
                "2",
                "testClass1",
                LocalDateTime.now(),
                "system",
                "testMethod",
                Map.of("key1", "value1", "key2", 123),
                Status.SUCCESS
        );
        HistoryContent content3 = new HistoryContent(
                "3",
                "testClass1",
                LocalDateTime.now(),
                "user",
                "testMethod",
                Map.of("key1", "value1", "key2", 123),
                Status.SUCCESS
        );

        // Сохраняем объекты
        repository.save(content1);
        repository.save(content2);
        repository.save(content3);


        // Ищем записи по actor
        List<HistoryContent> systemHistory = repository.findByActor("system");
        log.info(systemHistory.toString());

        // Проверяем, что возвращены правильные записи
        assertEquals(2, systemHistory.size(), "Неверное количество записей для user1");
        log.info(systemHistory.get(0).getCreatedDate().toString());
        assertTrue(systemHistory.stream().anyMatch(record ->
                record.getId().equals(content1.getId()) &&
                        record.getActor().equals(content1.getActor()) &&
                        record.getStatus() == content1.getStatus()
        ), "Список не содержит первую запись");
        assertTrue(systemHistory.stream().anyMatch(record ->
                record.getId().equals(content2.getId()) &&
                        record.getActor().equals(content2.getActor()) &&
                        record.getStatus() == content2.getStatus()
        ), "Список не содержит первую запись");

        repository.delete(content3);
        repository.delete(content2);
        repository.delete(content1);
    }*/


    /*@Test
    public void testSaveONE() {
        try {
            HistoryContent historyContent1 = new HistoryContent(
                    "testClass",
                    "testMethod",
                    Map.of("key1", "value1", "key2", 123),
                    Status.SUCCESS
            );
            MongoConverter<HistoryContent> converter = new MongoConverter<>(HistoryContent.class);
            MongoDataProvider<HistoryContent> provider = new MongoDataProvider<>(
                    MongoDBUtil.getCollection(
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
                    ),
                    converter
            );
            converter.serialize(historyContent1);

            HistoryObjectsRepository repository = HistoryObjectsRepository.defaultMongoRepository(MongoDBUtil.getCollection(
                    ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                    ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
            ));
            repository.save(historyContent1);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
}
