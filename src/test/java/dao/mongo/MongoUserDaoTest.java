package dao.mongo;

import com.carpooling.dao.mongo.MongoUserDao;
import com.carpooling.entities.database.Address;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoUserDaoTest {

    @Mock
    private MongoCollection<Document> mockCollection;
    @Mock
    private FindIterable<Document> mockFindIterable;

    @InjectMocks
    private MongoUserDao userDao;

    private User testUser;
    private Document testDocument;
    private String testUserIdStr;
    private UUID testUserIdUUID;


    @BeforeEach
    void setUp() {
        testUserIdUUID = UUID.randomUUID();
        testUserIdStr = testUserIdUUID.toString();

        Address testAddress = new Address();
        testAddress.setStreet("123 Main St");
        testAddress.setCity("Anytown");
        testAddress.setZipcode("12345");

        // Документ для встроенного адреса
        Document testAddressDocument = new Document()
                .append("street", "123 Main St")
                .append("city", "Anytown")
                .append("zipcode", "12345");

        testUser = new User();
        // testUser.setId(testUserIdUUID); // Устанавливается в createUser
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password"); // В тестах обычно не проверяется хеширование
        testUser.setGender("Other");
        testUser.setPhone("555-1234");
        testUser.setBirthDate(LocalDate.now().minusYears(25)); // 25 лет назад
        testUser.setAddress(testAddress);
        testUser.setPreferences("No smoking");


        testDocument = new Document()
                .append("id", testUserIdStr)
                .append("name", "Test User")
                .append("email", "test@example.com")
                .append("password", "password")
                .append("gender", "Other")
                .append("phone", "555-1234")
                .append("birthDate", testUser.getBirthDate())
                .append("address", testAddressDocument) // Вставляем документ адреса
                .append("preferences", "No smoking");
    }

    // --- Тесты для createUser ---

    @Test
    void createUser_Success_ShouldReturnGeneratedId() {
        // Arrange
        User userToCreate = new User();
        userToCreate.setName("New User");
        userToCreate.setEmail("new@example.com");
        Address newAddress = new Address();
        newAddress.setCity("New City");
        userToCreate.setAddress(newAddress);


        doAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            assertNotNull(doc.getString("id"));
            return null;
        }).when(mockCollection).insertOne(any(Document.class));

        // Act
        String createdId = userDao.createUser(userToCreate);

        // Assert
        assertNotNull(createdId);
        assertNotNull(userToCreate.getId());
        assertEquals(createdId, userToCreate.getId().toString());

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).insertOne(docCaptor.capture());
        Document insertedDoc = docCaptor.getValue();
        assertEquals(userToCreate.getId().toString(), insertedDoc.getString("id"));
        assertEquals("New User", insertedDoc.getString("name"));
        assertEquals("new@example.com", insertedDoc.getString("email"));
        assertNotNull(insertedDoc.get("address", Document.class));
        assertEquals("New City", insertedDoc.get("address", Document.class).getString("city"));
    }

    @Test
    void createUser_Failure_ShouldThrowDataAccessException() {
        // Arrange
        User userToCreate = new User();
        userToCreate.setName("Fail User");

        doThrow(new MongoException("DB connection error")).when(mockCollection).insertOne(any(Document.class));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDao.createUser(userToCreate);
        });
        assertTrue(exception.getMessage().contains("Error creating user"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        assertNotNull(userToCreate.getId());
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    // --- Тесты для getUserById ---

    @Test
    void getUserById_Found_ShouldReturnOptionalWithUser() {
        // Arrange
        when(mockCollection.find(eq(Filters.eq("id", testUserIdStr)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(testDocument);

        // Act
        Optional<User> result = userDao.getUserById(testUserIdStr);

        // Assert
        assertTrue(result.isPresent());
        User foundUser = result.get();
        assertEquals(testUserIdUUID, foundUser.getId());
        assertEquals("Test User", foundUser.getName());
        assertEquals("test@example.com", foundUser.getEmail());
        assertNotNull(foundUser.getAddress());
        assertEquals("123 Main St", foundUser.getAddress().getStreet());
        assertEquals("Anytown", foundUser.getAddress().getCity());
        assertEquals("12345", foundUser.getAddress().getZipcode());
        assertEquals("No smoking", foundUser.getPreferences());

        verify(mockCollection, times(1)).find(eq(Filters.eq("id", testUserIdStr)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getUserById_NotFound_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", nonExistentId)))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(null);

        // Act
        Optional<User> result = userDao.getUserById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", nonExistentId)));
        verify(mockFindIterable, times(1)).first();
    }

    @Test
    void getUserById_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.find(eq(Filters.eq("id", id)))).thenThrow(new MongoException("Query failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDao.getUserById(id);
        });
        assertTrue(exception.getMessage().contains("Error reading user"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).find(eq(Filters.eq("id", id)));
        verify(mockFindIterable, never()).first();
    }


    // --- Тесты для updateUser ---

    @Test
    void updateUser_Success_ShouldCallUpdateOne() {
        // Arrange
        testUser.setId(testUserIdUUID);
        testUser.setName("Updated User Name");
        testUser.getAddress().setZipcode("54321"); // Обновляем поле в адресе
        String userIdString = testUserIdUUID.toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq(Filters.eq("id", userIdString)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        assertDoesNotThrow(() -> userDao.updateUser(testUser));

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> updateCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mockCollection, times(1)).updateOne(filterCaptor.capture(), updateCaptor.capture());

        assertEquals(Filters.eq("id", userIdString), filterCaptor.getValue());
        Document setDoc = updateCaptor.getValue().get("$set", Document.class);
        assertNotNull(setDoc);
        assertEquals(userIdString, setDoc.getString("id"));
        assertEquals("Updated User Name", setDoc.getString("name"));
        assertNotNull(setDoc.get("address", Document.class));
        assertEquals("54321", setDoc.get("address", Document.class).getString("zipcode")); // Проверяем обновленный zipcode
        assertEquals("Anytown", setDoc.get("address", Document.class).getString("city")); // Проверяем старое поле адреса
    }

    @Test
    void updateUser_NotFound_ShouldThrowDataAccessException() {
        // Arrange
        testUser.setId(testUserIdUUID);
        String id = testUser.getId().toString();

        UpdateResult mockUpdateResult = mock(UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L);
        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenReturn(mockUpdateResult);

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDao.updateUser(testUser);
        });
        assertEquals("Error updating user", exception.getMessage());
        assertEquals("User not found", exception.getCause().getMessage());
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    @Test
    void updateUser_Failure_ShouldThrowDataAccessException() {
        // Arrange
        testUser.setId(testUserIdUUID);
        String id = testUser.getId().toString();

        when(mockCollection.updateOne(eq(Filters.eq("id", id)), any(Document.class)))
                .thenThrow(new MongoException("Update failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDao.updateUser(testUser);
        });
        assertTrue(exception.getMessage().contains("Error updating user"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).updateOne(eq(Filters.eq("id", id)), any(Document.class));
    }

    // --- Тесты для deleteUser ---

    @Test
    void deleteUser_Success_ShouldCallDeleteOne() {
        // Arrange
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", testUserIdStr))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> userDao.deleteUser(testUserIdStr));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", testUserIdStr)));
    }

    @Test
    void deleteUser_NotFound_ShouldCallDeleteOneAndNotThrow() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        DeleteResult mockDeleteResult = mock(DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(0L);
        when(mockCollection.deleteOne(eq(Filters.eq("id", nonExistentId))))
                .thenReturn(mockDeleteResult);

        // Act & Assert
        assertDoesNotThrow(() -> userDao.deleteUser(nonExistentId));
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", nonExistentId)));
    }

    @Test
    void deleteUser_Failure_ShouldThrowDataAccessException() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(mockCollection.deleteOne(eq(Filters.eq("id", id))))
                .thenThrow(new MongoException("Deletion failed"));

        // Act & Assert
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDao.deleteUser(id);
        });
        assertTrue(exception.getMessage().contains("Error deleting user"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MongoException);
        verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("id", id)));
    }
}