package com.aurora.oasisplanner.util.notificationfeatures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalTime;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        _Alarm alarm = _Alarm.unpackContents(intent.getBundleExtra("ALARM"));

        wakeDevice(context);

        AlarmNotificationService service = new AlarmNotificationService(context);
        service.showNotification(alarm);
    }

    public void wakeDevice(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock //waking the device;
                fullWakeLock = powerManager.newWakeLock((
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
        ), "Oasis:FullWakeLock");
        fullWakeLock.acquire(60*1000L);//1 minutes*/
    }
}
