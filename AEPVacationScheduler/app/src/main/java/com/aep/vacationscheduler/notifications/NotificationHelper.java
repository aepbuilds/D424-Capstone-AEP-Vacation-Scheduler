package com.aep.vacationscheduler.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import com.aep.vacationscheduler.data.Vacation;
import com.aep.vacationscheduler.data.Excursion;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * NotificationHelper provides utility methods for scheduling and showing
 * notifications for vacations and excursions.
 * Satisfies Requirement B3e and B5d.
 */
public class NotificationHelper {
    private static final String CHANNEL_ID = "vacation_notifications";

    /**
     * Schedules a notification for either the start or end date of a vacation [Requirement B3e].
     *
     * @param context The application context.
     * @param vacation The vacation object containing dates and title.
     * @param isStarting True if scheduling for the start date, false for the end date.
     */
    public static void scheduleVacationNotification(Context context, Vacation vacation, boolean isStarting) {
        String dateStr = isStarting ? vacation.startDate : vacation.endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", vacation.title);
            intent.putExtra("isStarting", isStarting);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)(Math.random()*1000), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules a notification for an excursion's date [Requirement B5d].
     *
     * @param context The application context.
     * @param excursion The excursion object containing the date and title.
     */
    public static void scheduleExcursionNotification(Context context, Excursion excursion) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date date = sdf.parse(excursion.date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", excursion.title);
            intent.putExtra("isStarting", true); // Generic trigger for excursion

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)(Math.random()*1000), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a notification to the user [Requirement B3e, B5d].
     *
     * @param context The application context.
     * @param title The title of the vacation.
     * @param isStarting True if the notification is for a start date, false for an end date.
     */
    public static void showNotification(Context context, String title, boolean isStarting) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vacations", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        String message = isStarting ? "Your vacation " + title + " is starting!" : "Your vacation " + title + " is ending!";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Vacation Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int)(Math.random()*1000), builder.build());
    }
}