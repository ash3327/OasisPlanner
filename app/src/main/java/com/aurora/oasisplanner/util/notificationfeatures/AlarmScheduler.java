package com.aurora.oasisplanner.util.notificationfeatures;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlarmScheduler {

    private Context context;
    private AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = context.getSystemService(AlarmManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    context.startActivity(intent);
                }
            }
        }
    }

    public void schedule(Alarm alarm) {

        if (LocalDateTime.now().isAfter(alarm.getDateTime())) return;
        Intent intent = new Intent(context, AlarmReceiver.class)
                .putExtra("ALARM", alarm.packContents());
        // still checks if alarm needs triggering even in low power mode.
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.getDateTime().withSecond(0).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                        context,
                        (int) alarm.getAlarmId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                )
        );
    }
    public static void scheduleMany(AlarmScheduler alarmScheduler, Alarm... alarms) {
        if (alarmScheduler == null)
            return;
        for (Alarm alarm : alarms)
            alarmScheduler.schedule(alarm);
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
    public static void cancelMany(AlarmScheduler alarmScheduler, _Alarm... alarms) {
        if (alarmScheduler == null)
            return;
        for (_Alarm alarm : alarms)
            alarmScheduler.cancel(alarm);
    }
}
