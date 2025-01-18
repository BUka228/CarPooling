package com.carpooling.entities.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    private String id;
    private int rating;
    private String comment;
    private Date date;
}
