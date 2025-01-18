package com.carpooling.entities.record;

import com.carpooling.entities.database.Route;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


/**
 * Класс RouteRecord представляет запись маршрута, используемую для обмена данными.
 * Он используется для сериализации/десериализации маршрутов в CSV и XML форматах.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "route")
public class RouteRecord {

    @CsvBindByName
    private String id;

    @CsvBindByName
    private String startPoint;

    @CsvBindByName
    private String endPoint;

    @CsvDate("yyyy-MM-dd HH:mm:ss") // Указываем формат даты
    @CsvBindByName
    private Date date;

    @CsvBindByName
    private short estimatedDuration;

    /**
     * Конструктор, создающий объект RouteRecord из объекта Route.
     * @param route Объект Route, из которого создается RouteRecord.
     */
    public RouteRecord(@NotNull Route route) {
        this.id = route.getId();
        this.startPoint = route.getStartPoint();
        this.endPoint = route.getEndPoint();
        this.date = route.getDate();
        this.estimatedDuration = route.getEstimatedDuration();
    }

    /**
     * Преобразует объект RouteRecord в объект Route.
     * @return Объект Route, созданный из RouteRecord.
     */
    public Route toRoute() {
        return new Route(id, startPoint, endPoint, date, estimatedDuration);
    }
}
