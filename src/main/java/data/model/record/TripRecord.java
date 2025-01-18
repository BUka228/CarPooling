package data.model.record;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import data.model.database.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "trip")
public class TripRecord {

    // Поля из Trip
    @CsvBindByName
    private String id;

    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date departureTime;

    @CsvBindByName
    private byte maxPassengers;

    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date creationDate;

    @CsvBindByName
    private String status;

    @CsvBindByName
    private boolean editable;

    // Дополнительные параметры

    @CsvBindByName
    private String userId;
    @CsvBindByName
    private String routeId;

    /**
     * Конструктор для удобного создания записи из объекта Trip.
     *
     * @param trip   Объект Trip.
     * @param userId ID пользователя.
     * @param routeId ID маршрута.
     */
    public TripRecord(@NotNull Trip trip, String userId, String routeId) {
        this.id = trip.getId();
        this.departureTime = trip.getDepartureTime();
        this.maxPassengers = trip.getMaxPassengers();
        this.creationDate = trip.getCreationDate();
        this.status = trip.getStatus();
        this.editable = trip.isEditable();
        this.userId = userId;
        this.routeId = routeId;
    }

    /**
     * Преобразует запись в объект Trip.
     *
     * @return Объект Trip.
     */
    public Trip toTrip() {
        return new Trip(
                id,
                departureTime,
                maxPassengers,
                creationDate,
                status,
                editable
        );
    }
}