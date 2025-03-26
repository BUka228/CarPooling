package dao.mongo;

import com.carpooling.dao.mongo.MongoBookingDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.entities.enums.BookingStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.MongoException;
import com.mongodb.client.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoBookingDaoTest {

    @Mock // Создаем mock-объект для зависимости
    private MongoCollection<Document> mockCollection;

    @Mock // Mock для FindIterable, возвращаемого find()
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoBookingDao bookingDao;

    private Booking testBooking;
    private Document testDocument;
    private String testBookingIdStr;
    private UUID testBookingIdUUID;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных перед каждым тестом
        testBookingIdUUID = UUID.randomUUID();
        testBookingIdStr = testBookingIdUUID.toString();

        testBooking = new Booking();
        // testBooking.setId(testBookingIdUUID); // ID устанавливается в createBooking
        testBooking.setNumberOfSeats((byte) 2);
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking.setBookingDate(LocalDateTime.now());
        testBooking.setPassportNumber("PN123456");
        testBooking.setPassportExpiryDate(LocalDate.now().plusYears(1)); // +1 год

        // Примерный документ, как он мог бы храниться/возвращаться из Mongo
        // Заметим, что AbstractMongoDao ожидает поле "id", а не "_id" после конвертации
        testDocument = new Document()
                .append("id", testBookingIdStr) // Используем строковый ID
                .append("numberOfSeats", (byte) 2)
                .append("status", "CONFIRMED")
                .append("bookingDate", testBooking.getBookingDate())
                .append("passportNumber", "PN123456")
                .append("passportExpiryDate", testBooking.getPassportExpiryDate());
    }

    // --- Тесты для createBooking ---

    @Test
    void createBooking_Success_ShouldReturnGeneratedId() {
        // Arrange
        Booking bookingToCreate = new Booking(); // Без ID
        bookingToCreate.setStatus(BookingStatus.PENDING);
        // ... другие поля

        // Мокаем insertOne - он не возвращает значение, но мы можем проверить вызов
        // и что ID был присвоен
        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            // Симулируем, что Mongo драйвер (или наш код) добавляет ID
            // В нашем DAO ID генерируется *до* toDocument, поэтому он уже будет в документе
            assertNotNull(doc.getString("id"), "ID должен быть установлен в документе перед вставкой");
            // Если бы ID генерировался базой, мы бы добавили его здесь:
            // doc.put("_id", new ObjectId());
            return null; // insertOne возвращает void
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = bookingDao.createBooking(bookingToCreate);

        // Assert
        assertNotNull(createdId, "Возвращенный ID не должен быть null");
        assertNotNull(bookingToCreate.getId(), "ID должен быть установлен в объекте Booking");
        assertEquals(createdId, bookingToCreate.getId().toString(), "Возвращенный ID должен совпадать с ID в объекте");

        // Проверяем, что insertOne был вызван ровно один раз с любым документом
        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());

        // Дополнительная проверка: убедимся, что документ содержит ожидаемые данные
        Document insertedDoc = docCaptor.getValue();
        assertEquals(bookingToCreate.getId().toString(), insertedDoc.getString("id"));
        assertEquals("PENDING", insertedDoc.getString("status"));
        // ... другие проверки полей при необходимости
    }

    @Test
    void createBooking_Failure_ShouldThrowDataAccessException() {
        // Arrange
        Booking bookingToCreate = new Booking();
        bookingToCreate.setStatus(BookingStatus.PENDING);

        // Мокаем insertOne, чтобы он выбрасывал исключение
        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bookingDao.createBooking(bookingToCreate);
        }, "Должно быть выброшено DataAccessException");

        // Дополнительно проверяем сообщение и причину исключения
        assertTrue(exception.getMessage().contains("Error creating booking"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("DB connection error", exception.getCause().getMessage());

        // Убедимся, что ID все равно был сгенерирован, но вставка не удалась
        assertNotNull(bookingToCreate.getId(), "ID должен был быть сгенерирован до попытки вставки");
        // Проверяем, что insertOne был вызван
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getBookingById ---

    @Test
    void getBookingById_Found_ShouldReturnOptionalWithBooking() {
        // Arrange
        // Мокаем цепочку вызовов find().first()
        when(mockCollection.find(eq(Filters.eq("id", testBookingIdStr)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument); // Возвращаем наш тестовый документ

        // Act
        Optional<Booking> result = bookingDao.getBookingById(testBookingIdStr);

        // Assert
        assertTrue(result.isPresent(), "Optional должен содержать значение");
        Booking foundBooking = result.get();
        assertEquals(testBookingIdUUID, foundBooking.getId(), "ID найденного бронирования не совпадает");
        assertEquals(testBooking.getStatus(), foundBooking.getStatus(), "Статус найденного бронирования не совпадает");
        // ... другие проверки полей
        // assertEquals(testBooking.getBookingDate(), foundBooking.getBookingDate()); // Осторожно со сравнением Date!

        // Проверяем, что find и first были вызваны
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", testBookingIdStr)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getBookingById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Мокаем цепочку вызовов find().first() так, чтобы first() вернул null
        when(mockCollection.find(eq(Filters.eq("id", nonExistentId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null); // Документ не найден

        // Act
        Optional<Booking> result = bookingDao.getBookingById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty(), "Optional должен быть пустым");

        // Проверяем, что find и first были вызваны
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", nonExistentId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getBookingById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        // Мокаем find, чтобы он выбрасывал исключение
        when(mockCollection.find(eq(Filters.eq("id", id)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bookingDao.getBookingById(id);
        });

        assertTrue(exception.getMessage().contains("Error reading booking"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Query failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).find(eq(Filters.eq("id", id)));
        // first() не будет вызван, т.к. find выбросил исключение
        verify(mockFindIterable, never()).first();
    }


    // --- Тесты для updateBooking ---

    @Test
    void updateBooking_Success_ShouldCallUpdateOne() {
        // Arrange
        testBooking.setId(testBookingIdUUID); // Устанавливаем ID для обновления
        testBooking.setStatus(BookingStatus.CANCELLED); // Обновляем поле
        String bookingIdString = testBookingIdUUID.toString(); // Получаем строковый ID

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L); // Симулируем успешное обновление

        when(mockCollection.updateOne(eq(Filters.eq("id", bookingIdString)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act
        // Проверяем, что метод НЕ выбрасывает исключение
        assertDoesNotThrow(() -> {
            bookingDao.updateBooking(testBooking);
        });

        // Assert
        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class); // <-- Тип Bson
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class); // Тип Document (остается)
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        Bson expectedFilter = Filters.eq("id", bookingIdString);
        assertEquals(expectedFilter, filterCaptor.getValue());

        // --- Проверка захваченного документа обновления ($set) ---
        Document updateDoc = updateCaptor.getValue();
        assertTrue(updateDoc.containsKey("$set"), "Обновление должно использовать $set");
        Document setDoc = updateDoc.get("$set", Document.class); // Безопасное получение вложенного документа
        assertNotNull(setDoc, "$set document should not be null");
        assertEquals(bookingIdString, setDoc.getString("id"), "ID в $set должен совпадать");
        assertEquals("CANCELLED", setDoc.getString("status"), "Статус в $set должен быть обновлен");
    }

    @Test
    void updateBooking_NotFound_ShouldThrowDataAccessException() {
        // Arrange
        testBooking.setId(testBookingIdUUID); // Устанавливаем ID
        String id = testBooking.getId().toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L); // Симулируем, что ни один документ не был обновлен
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bookingDao.updateBooking(testBooking);
        });

        assertEquals("Error updating booking", exception.getMessage()); // Проверяем сообщение об ошибке
        assertEquals("Booking not found", exception.getCause().getMessage());

        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    @Test
    void updateBooking_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testBooking.setId(testBookingIdUUID); // Устанавливаем ID
        String id = testBooking.getId().toString();

        // Мокаем updateOne, чтобы он выбрасывал исключение
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bookingDao.updateBooking(testBooking);
        });

        assertTrue(exception.getMessage().contains("Error updating booking"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Update failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }


    // --- Тесты для deleteBooking ---

    @Test
    void deleteBooking_Success_ShouldCallDeleteOne() {
        // Arrange
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L); // Симулируем успешное удаление
        when(mockCollection.deleteOne(eq(Filters.eq("id", testBookingIdStr))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> {
            bookingDao.deleteBooking(testBookingIdStr);
        });

        // Проверяем вызов deleteOne с правильным фильтром
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", testBookingIdStr)));
    }

    @Test
    void deleteBooking_NotFound_ShouldCallDeleteOneAndNotThrow() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(0L); // Симулируем, что ничего не удалено
        when(mockCollection.deleteOne(eq(Filters.eq("id", nonExistentId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        // Метод не должен выбрасывать исключение, даже если ничего не найдено (согласно логике DAO)
        assertDoesNotThrow(() -> {
            bookingDao.deleteBooking(nonExistentId);
        });

        // Проверяем вызов deleteOne
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", nonExistentId)));
    }

    @Test
    void deleteBooking_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        // Мокаем deleteOne, чтобы он выбрасывал исключение
        when(mockCollection.deleteOne(eq(Filters.eq("id", id))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bookingDao.deleteBooking(id);
        });

        assertTrue(exception.getMessage().contains("Error deleting booking"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertEquals("Deletion failed", exception.getCause().getMessage());

        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", id)));
    }
}