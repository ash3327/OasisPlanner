package com.aurora.oasisplanner.util.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Utils {

    @SuppressLint("RestrictedApi")
    public static void startPowerSaverIntent(Activity context, SharedPreferences.Editor editor, CountDownLatch latch, boolean allowSkip) {
        boolean foundCorrectIntent = false;
        for (Intent intent : Constants.POWERMANAGER_INTENTS) {
            if (isCallable(context, intent)) {
                foundCorrectIntent = true;
                context.startActivity(intent);
                break;
            }
        }
        if (!foundCorrectIntent) {
            editor.putBoolean("skipProtectedAppCheck", true);
            editor.apply();
        }
    }

    @SuppressLint("RestrictedApi")
    public static void postRequestDialog(Activity context, CountDownLatch latch, RequestDialogRunnable func, boolean allowSkip) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        AppCompatCheckBox dontShowAgain = null;
        if (allowSkip) {
            dontShowAgain = new AppCompatCheckBox(context);
            dontShowAgain.setText(R.string.options_permission_do_not_show_again);

            ColorStateList colorStateList = ColorStateList.valueOf(Color.BLACK);
            dontShowAgain.setSupportButtonTintList(colorStateList);

            dontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editor.putBoolean("skipProtectedAppCheck", isChecked);
                editor.apply();
            });
        }

        // show dialog
        new AlertDialog.Builder(context, R.style.WhiteDialogTheme)
                .setTitle(Build.MANUFACTURER + Resources.getString(R.string.warning_protected_apps))
                .setMessage(String.format(Resources.getString(R.string.warning_protected_apps_msg), context.getString(R.string.app_name)))
                .setView(dontShowAgain)
                .setPositiveButton(R.string.options_permission_settings, (dialog, which) -> func.run(editor))
                .setNegativeButton(R.string.options_permission_close, (x,v)->{if(latch!=null) latch.countDown();})
                .show();
    }

    public static void setSkipRequestPermission(Context context, boolean isChecked) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("skipProtectedAppCheck", isChecked);
        editor.apply();
    }

    public interface RequestDialogRunnable {
        void run(SharedPreferences.Editor editor);
    }

    private static boolean isCallable(Context context, Intent intent) {
        try {
            if (intent == null || context == null) {
                return false;
            } else {
                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }
}