package com.aep.vacationscheduler.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * NotificationReceiver is a BroadcastReceiver that handles the alarm triggers
 * scheduled by NotificationHelper and initiates the display of the notification.
 * Satisfies Requirement B3e and B5d.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract data from the intent passed by the AlarmManager
        String title = intent.getStringExtra("title");
        boolean isStarting = intent.getBooleanExtra("isStarting", true);

        // Delegate the actual notification display to NotificationHelper
        NotificationHelper.showNotification(context, title, isStarting);
    }
}