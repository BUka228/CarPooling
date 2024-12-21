package model.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private int id;
    private Date departureTime;
    private byte maxPassengers;
    private Date creationDate;
    private String status;
    private boolean editable;
}