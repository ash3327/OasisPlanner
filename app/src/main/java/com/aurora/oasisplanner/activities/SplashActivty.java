package com.aurora.oasisplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;

public class SplashActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        getSupportActionBar().hide();

        new LoadTask().execute("");
    }

    private class LoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //do what ever operations you want
            //just to create a cool splash activity for now that does nothing
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "result";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(SplashActivty.this, MainActivity.class);
            i.putExtra("data", result);
            startActivity(i);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }//*/
}