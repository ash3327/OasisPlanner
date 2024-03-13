package com.aurora.oasisplanner.util.permissions;

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

    public static void startPowerSaverIntent(Activity context, CountDownLatch latch) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        boolean foundCorrectIntent = false;
        for (Intent intent : Constants.POWERMANAGER_INTENTS) {
            if (isCallable(context, intent)) {
                foundCorrectIntent = true;
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                dontShowAgain.setText(R.string.options_permission_do_not_show_again);

                ColorStateList colorStateList = ColorStateList.valueOf(Color.BLACK);
                dontShowAgain.setSupportButtonTintList(colorStateList);

                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean("skipProtectedAppCheck", isChecked);
                        editor.apply();
                    }
                });

                AlertDialog dialog = new AlertDialog.Builder(context, R.style.WhiteDialogTheme)
                        .setTitle(Build.MANUFACTURER + Resources.getString(R.string.warning_protected_apps))
                        .setMessage(String.format(Resources.getString(R.string.warning_protected_apps_msg), context.getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton(R.string.options_permission_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivityForResult(intent, Constants.REQUEST_CODE_PERMISSIONS_SET);
                            }
                        })
                        .setNegativeButton(R.string.options_permission_close, (x,v)->latch.countDown())
                        .show();
                break;
            }
        }
        if (!foundCorrectIntent) {
            editor.putBoolean("skipProtectedAppCheck", true);
            editor.apply();
        }
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