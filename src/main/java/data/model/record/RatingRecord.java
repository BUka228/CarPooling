package data.model.record;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import data.model.database.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Класс RatingRecord представляет запись рейтинга, используемую для обмена данными.
 * Он используется для сериализации/десериализации рейтингов в CSV и XML форматах.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "rating")
public class RatingRecord {

    @CsvBindByName
    private String id;

    @CsvBindByName
    private int rating;

    @CsvBindByName
    private String comment;

    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date date;

    @CsvBindByName
    private String tripId;


    /**
     * Конструктор для создания объекта RatingRecord из объекта Rating.
     * @param rating Объект Rating, из которого будут взяты данные.
     * @param tripId - идентификатор поездки.
     */
    public RatingRecord(@NotNull Rating rating, String tripId) {
        this.id = rating.getId();
        this.rating = rating.getRating();
        this.comment = rating.getComment();
        this.date = rating.getDate();
        this.tripId = tripId;
    }

    /**
     * Преобразует объект RatingRecord в объект Rating.
     * @return Объект Rating, созданный из RatingRecord.
     */
    public Rating toRating() {
        return new Rating(id, rating, comment, date);
    }
}
