package com.aurora.oasisplanner.presentation.dialog.choosetypedialog;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.databinding.ChooseTypeBinding;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmNotificationService;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalTime;

public class ChooseTypeDialog {
    LinearLayout[] fabs = new LinearLayout[3];
    FloatingActionButton cross;
    AlertDialog dialog;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    float startX = Resources.getDimension(R.dimen.type_text_startX),
          startY = Resources.getDimension(R.dimen.type_text_startY);
    float[] endX = {
            Resources.getDimension(R.dimen.type_add_agenda_endX),
            Resources.getDimension(R.dimen.type_add_task_endX),
            Resources.getDimension(R.dimen.type_add_note_endX)
        }, endY = {
            Resources.getDimension(R.dimen.type_add_agenda_endY),
            Resources.getDimension(R.dimen.type_add_task_endY),
            Resources.getDimension(R.dimen.type_add_note_endY)
        };
    int durationStart = Resources.getInt(R.integer.duration_add_anim),
        durationEnd   = Resources.getInt(R.integer.duration_add_anim_end);

    public ChooseTypeDialog(){
        create();
    }

    /** The ChooseTypeDialog must be created before anything is done. */
    private void create(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.main);
        ChooseTypeBinding binding = ChooseTypeBinding.inflate(MainActivity.main.getLayoutInflater());

        binding.setDialog(this);
        ViewGroup vg = (ViewGroup) binding.getRoot();//(ViewGroup)MainActivity.main.getLayoutInflater().inflate(R.layout.choose_type, null);
        builder.setView(vg);
        dialog = builder.create();

        cross   = binding.cross;
        fabs[0] = binding.addAgenda;
        fabs[1] = binding.addTask;
        fabs[2] = binding.addNote;

        dialog.getWindow().setGravity(Gravity.TOP);
        vg.setOnClickListener(
                (agenda)->hideMenu()
        );
        cross.setOnClickListener(
                (agenda)->hideMenu()
        );
        dialog.setOnShowListener(
                (dialogInterface)->showMenu()
        );
    }

    public void show(){
        for (int i = 0 ; i < 3 ; i++) {
            fabs[i].setAlpha(0);
            fabs[i].setTranslationX(startX);
            fabs[i].setTranslationY(startY);
        }
        cross.setRotation(0);
        dialog.show();
    }
    private void showMenu(){
        for (int i = 0 ; i < 3 ; i++)
            fabs[i].animate().alpha(1).translationX(endX[i]).translationY(endY[i])
                    .setInterpolator(interpolator).setDuration(durationStart).start();
        cross.animate().rotation(45).setInterpolator(interpolator).setDuration(durationStart).start();
    }
    private void hideMenu(){
        for (int i = 0 ; i < 3 ; i++)
            fabs[i].animate().alpha(0).translationX(startX).translationY(startY)
                    .setInterpolator(interpolator).setDuration(durationEnd).start();
        cross.animate().rotation(0).setInterpolator(interpolator).setDuration(durationEnd).start();
        new Handler().postDelayed(()->dialog.dismiss(), durationEnd/2);
    }

    public void execute(int i) {
        hideMenu();
        switch (i) {
            case 0:
                AppModule.retrieveAgendaUseCases().edit(-1, -1);
                break;
            case 2:
                AppModule.retrieveMemoUseCases().edit(-1);
                break;
        }
    }
}
