package com.aep.vacationscheduler.data;

import android.app.Application;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppRepository acts as a mediator between the data sources (Room database)
 * and the rest of the application, abstracting the data access layer.
 * Satisfies Requirement B1 (Room Framework).
 */
public class AppRepository {
    private final VacationDao vacationDao;
    private final ExcursionDao excursionDao;

    // ExecutorService used to run database operations on background threads to avoid blocking the UI thread
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        vacationDao = db.vacationDao();
        excursionDao = db.excursionDao();
    }

    /**
     * Inserts a new vacation into the database asynchronously [Requirement B1].
     *
     * @param vacation The vacation object to insert.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void insertVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.insert(vacation);
            callback.run();
        });
    }

    /**
     * Updates an existing vacation in the database asynchronously [Requirement B1, B3b].
     *
     * @param vacation The vacation object with updated details.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void updateVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.update(vacation);
            callback.run();
        });
    }

    /**
     * Deletes a vacation from the database asynchronously [Requirement B1, B1b].
     *
     * @param vacation The vacation object to delete.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void deleteVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.delete(vacation);
            callback.run();
        });
    }

    /**
     * Retrieves all vacations from the database [Requirement C].
     *
     * @return A list of all Vacation objects.
     */
    public List<Vacation> getAllVacations() {
        return vacationDao.getAllVacations();
    }

    /**
     * Retrieves a specific vacation by its ID [Requirement B3a].
     *
     * @param id The ID of the vacation.
     * @return The Vacation object if found, otherwise null.
     */
    public Vacation getVacationById(int id) {
        return vacationDao.getVacationById(id);
    }

    /**
     * Inserts a new excursion into the database asynchronously [Requirement B3h].
     *
     * @param excursion The excursion object to insert.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void insertExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.insert(excursion);
            callback.run();
        });
    }

    /**
     * Updates an existing excursion in the database asynchronously [Requirement B5b].
     *
     * @param excursion The excursion object with updated details.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void updateExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.update(excursion);
            callback.run();
        });
    }

    /**
     * Deletes an excursion from the database asynchronously [Requirement B5b].
     *
     * @param excursion The excursion object to delete.
     * @param callback A runnable to execute on the UI thread after the operation completes.
     */
    public void deleteExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.delete(excursion);
            callback.run();
        });
    }

    /**
     * Retrieves all excursions associated with a specific vacation [Requirement B3g, C].
     *
     * @param vacationId The ID of the vacation.
     * @return A list of Excursion objects.
     */
    public List<Excursion> getExcursionsForVacation(int vacationId) {
        return excursionDao.getExcursionsForVacation(vacationId);
    }

    /**
     * Retrieves a specific excursion by its ID [Requirement B5a].
     *
     * @param id The ID of the excursion.
     * @return The Excursion object if found, otherwise null.
     */
    public Excursion getExcursionById(int id) {
        return excursionDao.getExcursionById(id);
    }

    /**
     * Counts the number of excursions associated with a specific vacation [Requirement B1b].
     *
     * @param vacationId The ID of the vacation.
     * @return The count of associated excursions.
     */
    public int getExcursionCountForVacation(int vacationId) {
        return excursionDao.getExcursionCountForVacation(vacationId);
    }
}
