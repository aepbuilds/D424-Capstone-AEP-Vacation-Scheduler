package com.aep.vacationscheduler.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Excursion entity representing an excursion record in the database.
 * Satisfies Requirement B1 (Room Framework).
 */
@Entity(tableName = "excursions",
        foreignKeys = @ForeignKey(entity = Vacation.class,
                parentColumns = "id",
                childColumns = "vacationId",
                onDelete = ForeignKey.CASCADE))
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int vacationId; // Reference to the associated vacation
    public String title;
    public String date;

    public Excursion(int vacationId, String title, String date) {
        this.vacationId = vacationId;
        this.title = title;
        this.date = date;
    }
}