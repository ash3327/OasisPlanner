package com.aurora.oasisplanner.data.model.pojo.events;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;

public class SubAlarm extends Alarm {
    @Embedded
    public _SubAlarm alarm;

    @Relation(parentColumn = "agendaId", entityColumn = "id", entity = _Agenda.class)
    public _Agenda agenda;
    @Relation(parentColumn = "activityId", entityColumn = "id", entity = _Activity.class)
    public _Activity activity;
    @Relation(parentColumn = "alarmListId", entityColumn = "id", entity = _Event.class)
    public _Event alarmList;

    public SubAlarm(){}

    public _Alarm getAlarm() { return alarm; }
    public _Agenda getAgenda() { return agenda; }
    public _Activity getActivity() { return activity; }
    public _Event getAlarmList() { return alarmList; }
    public void setAlarm(_Alarm alarm) { this.alarm = (_SubAlarm) alarm; }
    public void setAgenda(_Agenda agenda) { this.agenda = agenda; }
    public void setActivity(_Activity activity) { this.activity = activity; }
    public void setAlarmList(_Event alarmList) { this.alarmList = alarmList; }

    @Ignore
    public String toString() {
        return "<ALARM "+alarm+"\nALARMLIST "+alarmList+"\nACTIVITY "+activity+"\nAGENDA"+agenda+">";
    }
}
