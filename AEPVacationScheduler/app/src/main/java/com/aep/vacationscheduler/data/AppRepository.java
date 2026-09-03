package com.aep.vacationscheduler.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
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
    // Handler tied to the main looper so callbacks can safely touch UI/Activity state
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        vacationDao = db.vacationDao();
        excursionDao = db.excursionDao();
    }

    public void insertVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.insert(vacation);
            mainHandler.post(callback);
        });
    }

    public void updateVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.update(vacation);
            mainHandler.post(callback);
        });
    }

    public void deleteVacation(Vacation vacation, Runnable callback) {
        executorService.execute(() -> {
            vacationDao.delete(vacation);
            mainHandler.post(callback);
        });
    }

    public List<Vacation> getAllVacations() {
        return vacationDao.getAllVacations();
    }

    public Vacation getVacationById(int id) {
        return vacationDao.getVacationById(id);
    }

    public void insertExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.insert(excursion);
            mainHandler.post(callback);
        });
    }

    public void updateExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.update(excursion);
            mainHandler.post(callback);
        });
    }

    public void deleteExcursion(Excursion excursion, Runnable callback) {
        executorService.execute(() -> {
            excursionDao.delete(excursion);
            mainHandler.post(callback);
        });
    }

    public List<Excursion> getExcursionsForVacation(int vacationId) {
        return excursionDao.getExcursionsForVacation(vacationId);
    }

    public Excursion getExcursionById(int id) {
        return excursionDao.getExcursionById(id);
    }

    public int getExcursionCountForVacation(int vacationId) {
        return excursionDao.getExcursionCountForVacation(vacationId);
    }
}