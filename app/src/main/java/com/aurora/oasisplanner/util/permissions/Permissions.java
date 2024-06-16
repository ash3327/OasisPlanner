package com.aurora.oasisplanner.util.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.CountDownLatch;

public class Permissions {
    static final int REQUEST_CODE = 1;
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    public static void setupPermission(Activity activity, CountDownLatch latch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Permissions.requestExactAlarmPermission(activity);
            Permissions.requestNotificationPermission(activity);
            Permissions.requestPermissions(activity, Manifest.permission.SCHEDULE_EXACT_ALARM);
        }
        Permissions.requestPermissions(activity, Manifest.permission.WAKE_LOCK);
        //Permissions.requestPermissions(activity, Manifest.permission.BIN);
        //FIXED: alarmmanager-not-working-in-several-devices
        //  credit: OussaMah, 2018
        // on https://stackoverflow.com/questions/31638986/protected-apps-setting-on-huawei-phones-and-how-to-handle-it
        //TODO: This bug is fixed on Huawei but not on Samsung.
//        Permissions.requestPermissions(activity, latch, true);
    }
    public static void requestPermissions(Activity context, String permission) {
        try {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(context, new String[]{permission}, REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.e("permissions", "Permission "+permission+" Error");
        }
        Log.i("permissions", "Permission "+permission+" "+
                ((ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED)?
                        "denied":"granted"));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestExactAlarmPermission(Activity context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
            }
            Log.i("permissions", "Schedule Exact Alarm "+alarmManager.canScheduleExactAlarms());
        }
    }

    public static void requestNotificationPermission(Activity context) {
        // Check if the app has notification permission
//        boolean hasNotificationPermission = NotificationManagerCompat.from(context).areNotificationsEnabled();

//      if (!hasNotificationPermission) {
        // Request notification permission
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
    }

    public static void requestPermissions(Activity context, CountDownLatch latch, boolean allowSkip) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!(skipMessage && allowSkip)) {
            Utils.postRequestDialog(
                    context, latch,
                    (editor)->{
                        Utils.startPowerSaverIntent(context, editor, latch, allowSkip);
                        Permissions.setupPermission(context, latch);
                    },
                    allowSkip);
        } else if (latch != null) {
            latch.countDown();
        }
    }
}
