package model.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private int id;
    private String startPoint;
    private String endPoint;
    private Date date;
    private short estimatedDuration;
}
