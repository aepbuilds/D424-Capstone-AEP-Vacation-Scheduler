package com.aep.vacationscheduler.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * VacationDao provides the database access methods for Vacation entities.
 * Satisfies Requirement B1 (Room Framework).
 */
@Dao
public interface VacationDao {
    /**
     * Inserts a new vacation into the database.
     */
    @Insert
    void insert(Vacation vacation);

    /**
     * Updates an existing vacation record.
     */
    @Update
    void update(Vacation vacation);

    /**
     * Deletes a specific vacation record.
     */
    @Delete
    void delete(Vacation vacation);

    /**
     * Retrieves all vacations from the database, ordered by start date.
     *
     * @return A list of all Vacation objects.
     */
    @Query("SELECT * FROM vacations ORDER BY startDate ASC")
    List<Vacation> getAllVacations();

    /**
     * Retrieves a specific vacation by its ID.
     *
     * @param id The ID of the vacation.
     * @return The Vacation object.
     */
    @Query("SELECT * FROM vacations WHERE id = :id")
    Vacation getVacationById(int id);
}