package com.aurora.oasisplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule;

import java.util.concurrent.CountDownLatch;

public class SplashActivity extends AppCompatActivity {

    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //getSupportActionBar().hide();

        latch = new CountDownLatch(2);

        setupDatabase();

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

    public Intent handleRedirections() {
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String notificationMode = extras.getString(NotificationModule.NOTIFICATION_MODE);
            long agendaId = extras.getLong(NotificationModule.NOTIFICATION_CONTENT);
            long activityLId = extras.getLong(NotificationModule.NOTIFICATION_ACTIVITY);

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_MODE, notificationMode);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_CONTENT, agendaId);
            mainActivityIntent.putExtra(NotificationModule.NOTIFICATION_ACTIVITY, activityLId);
            return mainActivityIntent;
        } catch (Exception e) {}
        return new Intent(this, MainActivity.class);
    }

    /** setting up the database */
    private void setupDatabase() {
        // INFO: setup database and usecases
        AppDatabase db = AppModule.provideAppDatabase(getApplication());
        AlarmScheduler alarmScheduler = new AlarmScheduler(getApplicationContext());
        AgendaRepository agendaRepository = AppModule.provideAgendaRepository(db, alarmScheduler);
        AppModule.provideAgendaUseCases(agendaRepository);
        AppModule.provideAgendaUseCases(agendaRepository);

        // INFO: setup alarms
        AlarmRepository alarmRepository = AppModule.provideAlarmRepository(db);
        alarmRepository.schedule(alarmScheduler, this, latch);
    }
}