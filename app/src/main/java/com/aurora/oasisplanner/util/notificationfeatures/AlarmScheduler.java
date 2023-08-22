package com.aurora.oasisplanner.util.notificationfeatures;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.aurora.oasisplanner.data.model.entities._Alarm;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlarmScheduler {

    private Context context;
    private AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = context.getSystemService(AlarmManager.class);
    }

    @SuppressLint("MissingPermission")
    public void schedule(_Alarm alarm) {

        if (LocalDateTime.now().isAfter(alarm.datetime)) return;
        Intent intent = new Intent(context, AlarmReceiver.class)
                .putExtra("ALARM", alarm.packContents());
        // still checks if alarm needs triggering even in low power mode.
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.datetime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                        context,
                        (int) alarm.id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                )
        );
    }

    public void cancel(_Alarm alarm) {
        alarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        (int) alarm.id,
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                )
        );
    }
}
