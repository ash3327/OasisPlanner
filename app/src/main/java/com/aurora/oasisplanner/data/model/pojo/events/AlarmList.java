package com.aurora.oasisplanner.data.model.pojo.events;

import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo._Entity;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AlarmList extends _Entity {
    @Embedded
    public _AlarmList alarmList;

    @Relation(parentColumn = "id", entityColumn = "alarmListId")
    public List<_Alarm> alarms = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "alarmListId")
    public List<_SubAlarm> subalarms = new ArrayList<>();

    @Ignore
    public boolean visible = true;
    @Ignore
    public SpannableStringBuilder contents;

    public AlarmList(){}

    @Ignore
    public AlarmList(AlarmType type, Importance importance) {
        this.alarmList = new _AlarmList(type, importance);
    }

    @Ignore
    public AlarmList putDates(LocalTime time, List<LocalDate> dates) {
        return putDates(time, dates.toArray(new LocalDate[0]));
    }
    @Ignore
    public AlarmList putDates(LocalTime time, LocalDate... dates) {
        for (_Alarm alarm : this.alarms)
            alarm.visible = false;
        for (_SubAlarm subAlarm : this.subalarms)
            subAlarm.visible = false;
        this.alarmList.time = time;
        this.alarmList.dates = Arrays.stream(dates).collect(Collectors.toList());

        this.alarms.addAll(Arrays.stream(dates).map((d)->new _Alarm().setDateTime(d, alarmList.time)).collect(Collectors.toList()));
        this.subalarms.addAll(_SubAlarm.generateSubAlarms(this));

        return this;
    }
    @Ignore
    public AlarmList setSubalarms() {
        for (_SubAlarm subAlarm : this.subalarms)
            subAlarm.visible = false;
        this.subalarms.addAll(_SubAlarm.generateSubAlarms(this));
        return this;
    }

    @Ignore
    public static AlarmList empty() {
        return new AlarmList(
                AlarmType.notif,
                Importance.regular
            ).putDates(LocalTime.now(), LocalDate.now());
    }

    public AlarmList setI(int i) {
        this.alarmList.i = i;
        return this;
    }

    @Ignore
    public String toString() {
        return "\n\t\t\t\t [ AlarmList : "+alarmList.id+" :  \n\t\t\t\t\t"+alarmList.type+","+alarmList.importance+" \n\t\t\t\t\t"+alarms.toString()+"\n\t\t\t\t ]";
    }
}
