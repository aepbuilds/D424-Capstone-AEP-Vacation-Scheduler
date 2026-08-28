package com.aep.vacationscheduler.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * ExcursionDao provides the database access methods for Excursion entities.
 * Satisfies Requirement B1 (Room Framework).
 */
@Dao
public interface ExcursionDao {
    /**
     * Inserts a new excursion into the database.
     */
    @Insert
    void insert(Excursion excursion);

    /**
     * Updates an existing excursion record.
     */
    @Update
    void update(Excursion excursion);

    /**
     * Deletes a specific excursion record.
     */
    @Delete
    void delete(Excursion excursion);

    /**
     * Retrieves all excursions associated with a specific vacation, ordered by date.
     *
     * @param vacationId The ID of the vacation.
     * @return A list of associated excursions.
     */
    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY date ASC")
    List<Excursion> getExcursionsForVacation(int vacationId);

    /**
     * Retrieves a specific excursion by its ID.
     *
     * @param id The ID of the excursion.
     * @return The Excursion object.
     */
    @Query("SELECT * FROM excursions WHERE id = :id")
    Excursion getExcursionById(int id);

    /**
     * Counts the number of excursions associated with a specific vacation.
     * Used to validate if a vacation can be deleted.
     *
     * @param vacationId The ID of the vacation.
     * @return The number of excursions.
     */
    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int getExcursionCountForVacation(int vacationId);
}