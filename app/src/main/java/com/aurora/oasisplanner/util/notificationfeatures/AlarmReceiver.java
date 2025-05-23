package com.aurora.oasisplanner.util.notificationfeatures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        wakeDevice(context);
        if (Objects.equals(intent.getAction(), NotificationModule.NOTIFICATION_EVENT)) {
            Alarm alarm = Alarm.unpackContents(intent.getBundleExtra("ALARM"));

            wakeDevice(context);

            Intent serviceIntent = new Intent(context, AlarmNotificationService.class);
            serviceIntent.putExtra("ALARM", alarm.packContents());
            context.startService(serviceIntent);

            if (MainActivity.main != null)
                new Handler().postDelayed(() -> MainActivity.main.navigateTo(MainActivity.page), 1000);
        }
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
