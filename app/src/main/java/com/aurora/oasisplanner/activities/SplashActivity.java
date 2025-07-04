package com.aurora.oasisplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule;
import com.aurora.oasisplanner.util.permissions.Constants;
import com.aurora.oasisplanner.util.permissions.Permissions;

import java.util.concurrent.CountDownLatch;

public class SplashActivity extends AppCompatActivity {

    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //getSupportActionBar().hide();

        latch = new CountDownLatch(3);

        Permissions.requestPermissions(this, latch, true);
        AppModule.setupDatabase(getApplication(), this, latch);

        new Thread(()->{
            try {
                latch.await();

                Intent i = handleRedirections();
                //i.putExtra("data", result);
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(latch::countDown, 600);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_SET || requestCode == Permissions.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (latch != null) latch.countDown();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Intent handleRedirections() {
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String notificationMode = extras.getString(NotificationModule.NOTIFICATION_MODE);
            long agendaId = extras.getLong(NotificationModule.NOTIFICATION_CONTENT);
            long activityLId = extras.getLong(NotificationModule.NOTIFICATION_ACTIVITY);
            long eventLId = extras.getLong(NotificationModule.NOTIFICATION_EVENT);

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_MODE, notificationMode);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_CONTENT, agendaId);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_ACTIVITY, activityLId);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_EVENT, eventLId);
            return mainActivityIntent;
        } catch (Exception e) {}
        return new Intent(this, MainActivity.class);
    }


}