package dao.mongo;

import com.carpooling.dao.mongo.MongoBookingDao;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID; // Используется в Booking, но для Mongo ID будет ObjectId/String

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Чтобы @BeforeAll/@AfterAll не были static
class MongoBookingDaoTest {

    private static final String DATABASE_NAME = "test_booking_db";
    private static final String COLLECTION_NAME = "bookings";

    private MongodExecutable mongodExecutable;
    private MongodProcess mongodProcess;
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private MongoBookingDao bookingDao;

    @BeforeAll
    void setUpAll() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        String bindIp = "localhost";
        int port = Network.getFreeServerPort(); // Найти свободный порт

        ImmutableMongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.V6_0) // Используйте нужную вам версию MongoDB
                .net(new de.flapdoodle.embed.mongo.config.Net(bindIp, port, Network.localhostIsIPv6()))
                .build();

        this.mongodExecutable = starter.prepare(mongodConfig);
        this.mongodProcess = mongodExecutable.start();
        this.mongoClient = MongoClients.create("mongodb://" + bindIp + ":" + port);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    @BeforeEach
    void setUp() {
        // Очистка коллекции перед каждым тестом для изоляции
        collection.drop();
        // Создаем DAO с чистой коллекцией
        bookingDao = new MongoBookingDao(collection);
    }

    @AfterAll
    void tearDownAll() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
        if (this.mongodProcess != null) {
            this.mongodProcess.stop();
        }
        if (this.mongodExecutable != null) {
            this.mongodExecutable.stop();
        }
    }

    // Вспомогательный метод для создания тестового Booking
    private Booking createTestBooking() {
        Booking booking = new Booking();
        // Устанавливаем поля сущности
        booking.setNumberOfSeats((byte) 2);
        booking.setStatus("pending");
        booking.setBookingDate(new Date());
        booking.setPassportNumber("PN987654");
        booking.setPassportExpiryDate(new Date(System.currentTimeMillis() + 31536000000L)); // +1 year
        // Связанные сущности (Trip, User) в MongoDB обычно хранятся как ID или вложенные документы.
        // В вашей сущности они @JsonIgnore/@XmlTransient, так что они не должны сохраняться напрямую.
        // Если нужно хранить ID, добавьте соответствующие поля String/UUID в Booking.
        // booking.setTripId(UUID.randomUUID().toString()); // Пример, если бы было поле tripId
        // booking.setUserId(UUID.randomUUID().toString()); // Пример, если бы было поле userId
        return booking;
    }

    @Test
    void createBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        assertNotNull(id);
        assertTrue(ObjectId.isValid(id), "Returned ID should be a valid ObjectId hex string");

        // Проверка наличия документа в БД
        Document savedDoc = collection.find(com.mongodb.client.model.Filters.eq("_id", new ObjectId(id))).first();
        assertNotNull(savedDoc, "Booking document should exist in the database");
        assertEquals("pending", savedDoc.getString("status"));
        assertEquals(2, savedDoc.getInteger("numberOfSeats")); // MongoDB хранит byte как Integer
        assertEquals("PN987654", savedDoc.getString("passportNumber"));

        // Проверка через getBookingById
        Optional<Booking> foundOpt = bookingDao.getBookingById(id);
        assertTrue(foundOpt.isPresent());
        Booking found = foundOpt.get();
        assertEquals(id, found.getId()); // ID должен быть установлен в объекте
        assertEquals(booking.getStatus(), found.getStatus());
        assertEquals(booking.getNumberOfSeats(), found.getNumberOfSeats());
        assertEquals(booking.getPassportNumber(), found.getPassportNumber());
        assertNotNull(found.getBookingDate());
        assertNotNull(found.getPassportExpiryDate());
    }

    @Test
    void getBookingById_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);

        Optional<Booking> foundOpt = bookingDao.getBookingById(id);

        assertTrue(foundOpt.isPresent());
        assertEquals(id, foundOpt.get().getId());
        assertEquals("pending", foundOpt.get().getStatus());
    }

    @Test
    void getBookingById_NotFound() throws DataAccessException {
        String nonExistentId = new ObjectId().toHexString(); // Валидный, но несуществующий ID
        Optional<Booking> foundOpt = bookingDao.getBookingById(nonExistentId);

        assertFalse(foundOpt.isPresent());
    }

    @Test
    void getBookingById_InvalidIdFormat_ShouldThrowException() {
        String invalidId = "this-is-not-an-object-id";
        // Ожидаем DataAccessException, так как new ObjectId(id) упадет с IllegalArgumentException
        assertThrows(DataAccessException.class, () -> bookingDao.getBookingById(invalidId));
    }

    @Test
    void updateBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);
        ObjectId objectId = new ObjectId(id);

        // Получаем, модифицируем и обновляем
        Booking toUpdate = bookingDao.getBookingById(id).orElseThrow();
        toUpdate.setStatus("confirmed");
        toUpdate.setNumberOfSeats((byte) 1);

        // Важно: ID должен быть установлен в объекте toUpdate для метода updateBooking
        // В AbstractMongoDao.fromDocument он устанавливается, так что все ок.

        bookingDao.updateBooking(toUpdate);

        // Проверяем напрямую в БД
        Document updatedDoc = collection.find(com.mongodb.client.model.Filters.eq("_id", objectId)).first();
        assertNotNull(updatedDoc);
        assertEquals("confirmed", updatedDoc.getString("status"));
        assertEquals(1, updatedDoc.getInteger("numberOfSeats")); // byte -> Integer

        // Проверяем через getBookingById
        Optional<Booking> foundOpt = bookingDao.getBookingById(id);
        assertTrue(foundOpt.isPresent());
        assertEquals("confirmed", foundOpt.get().getStatus());
        assertEquals((byte) 1, foundOpt.get().getNumberOfSeats());
    }

    @Test
    void updateBooking_NotFound() {
        Booking booking = createTestBooking();
        // НЕ создаем его в DAO
        booking.setId(new ObjectId().toHexString()); // Устанавливаем несуществующий, но валидный ID

        // Ожидаем исключение, так как updateOne не найдет документ
        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(booking));
    }

    @Test
    void deleteBooking_Success() throws DataAccessException {
        Booking booking = createTestBooking();
        String id = bookingDao.createBooking(booking);
        ObjectId objectId = new ObjectId(id);

        // Убедимся, что запись существует
        assertEquals(1, collection.countDocuments(com.mongodb.client.model.Filters.eq("_id", objectId)));

        bookingDao.deleteBooking(id);

        // Убедимся, что запись удалена
        assertEquals(0, collection.countDocuments(com.mongodb.client.model.Filters.eq("_id", objectId)));
        Optional<Booking> foundOpt = bookingDao.getBookingById(id);
        assertFalse(foundOpt.isPresent());
    }

    @Test
    void deleteBooking_NotFound() {
        String nonExistentId = new ObjectId().toHexString();

        // Проверяем, что удаление несуществующего ID не вызывает исключение (согласно коду DAO)
        assertDoesNotThrow(() -> bookingDao.deleteBooking(nonExistentId));

        // Убедимся, что ничего не было удалено (если коллекция была пуста)
        assertEquals(0, collection.countDocuments());
    }

    @Test
    void deleteBooking_InvalidIdFormat_ShouldThrowException() {
        String invalidId = "invalid-id";
        // Ожидаем DataAccessException, так как new ObjectId(id) упадет
        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking(invalidId));
    }
}
