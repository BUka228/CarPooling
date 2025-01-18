package data.model.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String id;
    private byte seatCount;
    private String status;
    private Date bookingDate;
    private String passportNumber;
    private Date passportExpiryDate;
}

