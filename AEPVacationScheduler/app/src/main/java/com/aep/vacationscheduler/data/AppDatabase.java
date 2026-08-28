package com.aep.vacationscheduler.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * AppDatabase is the main Room database class that manages the application's local SQLite database.
 * It defines the entities involved and provides access to the DAOs.
 */
@Database(entities = {Vacation.class, Excursion.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VacationDao vacationDao();
    public abstract ExcursionDao excursionDao();

    private static volatile AppDatabase INSTANCE;

    /**
     * Singleton method to provide the database instance.
     * Uses double-checked locking to ensure thread-safe initialization.
     *
     * @param context The application context used to build the database.
     * @return The singleton instance of AppDatabase.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "vacation_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}