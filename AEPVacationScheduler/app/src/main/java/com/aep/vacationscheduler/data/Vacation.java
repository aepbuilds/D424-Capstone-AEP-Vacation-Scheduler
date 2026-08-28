package com.aep.vacationscheduler.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Vacation entity representing a vacation record in the database.
 * Satisfies Requirement B1 (Room Framework).
 */
@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String hotel;
    public String startDate;
    public String endDate;

    public Vacation(String title, String hotel, String startDate, String endDate) {
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}