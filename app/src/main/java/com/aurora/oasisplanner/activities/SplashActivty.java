package com.aurora.oasisplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.concurrent.CountDownLatch;

public class SplashActivty extends AppCompatActivity {

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

                Intent i = new Intent(SplashActivty.this, MainActivity.class);
                //i.putExtra("data", result);
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(latch::countDown, 600);
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