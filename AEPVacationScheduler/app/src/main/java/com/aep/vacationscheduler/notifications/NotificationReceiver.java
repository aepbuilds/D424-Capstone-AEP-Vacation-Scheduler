package com.aep.vacationscheduler.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        boolean isStarting = intent.getBooleanExtra("isStarting", true);

        NotificationHelper.showNotification(context, title, isStarting);
    }
}