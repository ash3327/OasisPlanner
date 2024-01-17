package com.aurora.oasisplanner.data.model.pojo.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubAlarm extends Alarm {
    @Embedded
    public _SubAlarm alarm;

    @Relation(parentColumn = "agendaId", entityColumn = "id", entity = _Agenda.class)
    public _Agenda agenda;
    @Relation(parentColumn = "activityId", entityColumn = "id", entity = _Activity.class)
    public _Activity activity;
    @Relation(parentColumn = "alarmListId", entityColumn = "id", entity = _AlarmList.class)
    public _AlarmList alarmList;

    public SubAlarm(){}

    public _Alarm getAlarm() { return alarm; }
    public _Agenda getAgenda() { return agenda; }
    public _Activity getActivity() { return activity; }
    public _AlarmList getAlarmList() { return alarmList; }
    public void setAlarm(_Alarm alarm) { this.alarm = (_SubAlarm) alarm; }
    public void setAgenda(_Agenda agenda) { this.agenda = agenda; }
    public void setActivity(_Activity activity) { this.activity = activity; }
    public void setAlarmList(_AlarmList alarmList) { this.alarmList = alarmList; }

    @Ignore
    public String toString() {
        return "<ALARM "+alarm+"\nALARMLIST "+alarmList+"\nACTIVITY "+activity+"\nAGENDA"+agenda+">";
    }
}
