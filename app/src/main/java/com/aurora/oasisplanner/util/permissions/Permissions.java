package com.aurora.oasisplanner.util.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class Permissions {
    public static void setupPermission(Context context, Activity activity, CountDownLatch latch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Permissions.requestExactAlarmPermission(context);
            Permissions.requestPermissions(activity, Manifest.permission.SCHEDULE_EXACT_ALARM);
        }
        Permissions.requestPermissions(activity, Manifest.permission.WAKE_LOCK);
        //Permissions.requestPermissions(activity, Manifest.permission.BIN);
        //FIXED: alarmmanager-not-working-in-several-devices
        //  credit: OussaMah, 2018
        // on https://stackoverflow.com/questions/31638986/protected-apps-setting-on-huawei-phones-and-how-to-handle-it
        Permissions.requestProtectedAppsPermission(activity, latch, true);
    }
    public static void requestPermissions(Activity context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, 0);
        }
        Log.i("permissions", "Permission "+permission+" "+
                ((ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED)?
                        "denied":"granted"));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestExactAlarmPermission(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
            }
        }
    }

    public static void requestProtectedAppsPermission(Activity context, CountDownLatch latch, boolean allowSkip) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!(skipMessage && allowSkip)) {
            Utils.startPowerSaverIntent(context, latch, allowSkip);
        } else {
            latch.countDown();
        }
    }
}
