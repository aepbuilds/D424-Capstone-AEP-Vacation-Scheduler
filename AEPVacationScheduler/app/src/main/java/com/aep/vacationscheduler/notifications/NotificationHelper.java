package com.aep.vacationscheduler.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.aep.vacationscheduler.data.Vacation;
import com.aep.vacationscheduler.data.Excursion;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {
    private static final String CHANNEL_ID = "vacation_notifications";

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleVacationNotification(Context context, Vacation vacation, boolean isStarting) {
        String dateStr = isStarting ? vacation.startDate : vacation.endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            assert date != null;
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

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleExcursionNotification(Context context, Excursion excursion) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date date = sdf.parse(excursion.date);
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", excursion.title);
            intent.putExtra("isStarting", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)(Math.random()*1000), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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