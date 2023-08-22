package com.aurora.oasisplanner.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.aurora.oasisplanner.util.notificationfeatures.AlarmNotificationService;

public class OasisApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        // target version is >= O already
        NotificationChannel channel = new NotificationChannel(
                AlarmNotificationService.ALARM_CHANNEL_ID,
                AlarmNotificationService.ALARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Used for posting alarm notifications about an Agenda event");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
