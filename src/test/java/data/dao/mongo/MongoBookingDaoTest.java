package data.dao.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import data.dao.fake.FakeFindIterable;
import data.model.database.Booking;
import data.model.record.BookingRecord;
import exceptions.dao.DataAccessException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoBookingDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private ObjectMapper objectMapper;

    private MongoBookingDao bookingDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDao = new MongoBookingDao(collection);
    }

    @Test
    void testCreateBooking_Success() throws JsonProcessingException {

        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setTripId(new ObjectId().toHexString());
        bookingRecord.setUserId(new ObjectId().toHexString());
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");

        Document document = new Document("_id", new ObjectId())
                .append("tripId", new ObjectId(bookingRecord.getTripId()))
                .append("userId", new ObjectId(bookingRecord.getUserId()))
                .append("seatCount", bookingRecord.getSeatCount())
                .append("status", bookingRecord.getStatus());

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.put("_id", new ObjectId());
            return null;
        });

        String bookingId = bookingDao.createBooking(bookingRecord);

        assertNotNull(bookingId);
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testCreateBooking_Failure() throws JsonProcessingException {

        BookingRecord bookingRecord = new BookingRecord();
        bookingRecord.setTripId(new ObjectId().toHexString());
        bookingRecord.setUserId(new ObjectId().toHexString());
        bookingRecord.setSeatCount((byte) 2);
        bookingRecord.setStatus("CONFIRMED");

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.insertOne(any(Document.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> bookingDao.createBooking(bookingRecord));
        verify(collection, times(1)).insertOne(any(Document.class));
    }

    @Test
    void testGetBookingById_Success() throws JsonProcessingException {

        String bookingId = new ObjectId().toHexString();
        BookingRecord bookingRecord = new BookingRecord(
                bookingId,
                (byte) 2,
                "CONFIRMED",
                new Date(),
                "A12345678",
                new Date(),
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );

        Document document = toDocument(bookingRecord);
        document.put("_id", new ObjectId(bookingId));

        when(collection.find(eq(new ObjectId(bookingId)))).thenReturn(new FakeFindIterable(document));

        Optional<BookingRecord> booking = bookingDao.getBookingById(bookingId);

        assertTrue(booking.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(bookingId)));
    }

    @Test
    void testGetBookingById_NotFound() {
        String bookingId = new ObjectId().toHexString();

        when(collection.find(eq(new ObjectId(bookingId)))).thenReturn(new FakeFindIterable(null));

        Optional<BookingRecord> booking = bookingDao.getBookingById(bookingId);

        assertFalse(booking.isPresent());
        verify(collection, times(1)).find(eq(new ObjectId(bookingId)));
    }

    @Test
    void testUpdateBooking_Success() throws JsonProcessingException {
        BookingRecord bookingRecord = new BookingRecord(
                new ObjectId().toHexString(),
                (byte) 2,
                "CONFIRMED",
                new Date(),
                "A12345678",
                new Date(),
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        bookingDao.updateBooking(bookingRecord);

        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testUpdateBooking_Failure() throws JsonProcessingException {
        BookingRecord bookingRecord = new BookingRecord(
                new ObjectId().toHexString(),
                (byte) 2,
                "CONFIRMED",
                new Date(),
                "A12345678",
                new Date(),
                new ObjectId().toHexString(),
                new ObjectId().toHexString()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(collection.updateOne((Bson) any(), (Bson) any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> bookingDao.updateBooking(bookingRecord));
        verify(collection, times(1)).updateOne((Bson) any(), (Bson) any());
    }

    @Test
    void testDeleteBooking_Success() {
        String bookingId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(bookingId)))).thenReturn(DeleteResult.acknowledged(1L));

        bookingDao.deleteBooking(bookingId);

        verify(collection, times(1)).deleteOne(eq(new ObjectId(bookingId)));
    }

    @Test
    void testDeleteBooking_Failure() {
        String bookingId = new ObjectId().toHexString();

        when(collection.deleteOne(eq(new ObjectId(bookingId)))).thenThrow(new RuntimeException("DB error"));

        assertThrows(DataAccessException.class, () -> bookingDao.deleteBooking(bookingId));
        verify(collection, times(1)).deleteOne(eq(new ObjectId(bookingId)));
    }

    protected Document toDocument(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(object);
            Document document = Document.parse(json);
            document.remove("id");
            return document;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось преобразовать объект в документ.", e);
        }
    }

    protected <T> T fromDocument(Document document, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (document.containsKey("_id")) {
                document.put("id", document.getObjectId("_id").toHexString());
                document.remove("_id");
            }
            String json = document.toJson();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось преобразовать документ в объект.", e);
        }
    }
}
