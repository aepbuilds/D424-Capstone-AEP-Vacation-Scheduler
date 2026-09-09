package com.aep.vacationscheduler.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VacationDao {

    @Insert
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY startDate ASC")
    List<Vacation> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :id")
    Vacation getVacationById(int id);
}