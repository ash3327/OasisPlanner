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
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.aurora.oasisplanner.util.styling.Styles;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class Alarm {
    @Embedded
    public _Alarm alarm;
    
    @Relation(parentColumn = "agendaId", entityColumn = "id", entity = _Agenda.class)
    public _Agenda agenda;
    @Relation(parentColumn = "activityId", entityColumn = "id", entity = _Activity.class)
    public _Activity activity;
    @Relation(parentColumn = "alarmListId", entityColumn = "id", entity = _AlarmList.class)
    public _AlarmList alarmList;

    public Alarm(){}

    public _Alarm getAlarm() { return alarm; }
    public _Agenda getAgenda() { return agenda; }
    public _Activity getActivity() { return activity; }
    public _AlarmList getAlarmList() { return alarmList; }
    public void setAlarm(_Alarm alarm) { this.alarm = alarm; }
    public void setAgenda(_Agenda agenda) { this.agenda = agenda; }
    public void setActivity(_Activity activity) { this.activity = activity; }
    public void setAlarmList(_AlarmList alarmList) { this.alarmList = alarmList; }

    public AlarmType getType() {
        return getAlarmList().type;
    }
    public Importance getImportance() {
        return getAlarmList().importance;
    }
    public LocalDateTime getDateTime() {
        return getAlarm().datetime;
    }
    public LocalDate getDate() {
        return getAlarm().date;
    }

    public String getTitle() {
        return getAgenda().title;
    }
    public SpannableStringBuilder getAgendaDescr() {
        return new SpannableStringBuilder();
    }
    public SpannableStringBuilder getAlarmDescr() {
        return getActivity().descr;
    }
    @Ignore
    public SpannableStringBuilder getContents(boolean inExpandedMode) {
        return inExpandedMode ?
                getAgendaDescr().toString().isEmpty() ?
                        new SpannableStringBuilder()
                                .append(getAlarmDescr()) :
                        new SpannableStringBuilder()
                                .append(getAlarmDescr())
                                .append("\n\n")
                                .append(getAgendaDescr())
                : Styles.truncate(getAlarmDescr(), 12);
    }
    @Ignore
    public String getTagsString() {
        String out = "";
        out += getAlarmList().getTagsString();
        return out.isEmpty() || out.equals(null + "") ? null : out;
    }
    @Ignore
    public SpannableStringBuilder getLoc() {
        return getAlarmList().getLoc();
    }

    public long getAgendaId() {
        return getAgenda().id;
    }
    public long getActivityId() {
        return getActivity().id;
    }
    public long getAlarmListId() {
        return getAlarmList().id;
    }
    public long getAlarmId() {
        return getAlarm().id;
    }
    public long getEncodedId() {
        return getAlarmId() * 2 + (getAlarm().isSubalarm()?0:1);
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        extras.putBundle("alarm", getAlarm().packContents());
        extras.putBundle("alarmList", getAlarmList().packContents());
        extras.putBundle("activity", getActivity().packContents());
        extras.putBundle("agenda", getAgenda().packContents());

        return extras;
    }

    @Ignore
    public static void unpackExceptAlarm(Alarm alarm, Bundle extras) {
        alarm.setAlarmList(_AlarmList.unpackContents(extras.getBundle("alarmList")));
        alarm.setActivity(_Activity.unpackContents(extras.getBundle("activity")));
        alarm.setAgenda(_Agenda.unpackContents(extras.getBundle("agenda")));
    }
    @Ignore
    public static Alarm unpackContents(Bundle extras) {
        Alarm alarm = new Alarm();

        alarm.setAlarm(_Alarm.unpackContents(extras.getBundle("alarm")));
        unpackExceptAlarm(alarm, extras);

        return alarm;
    }

    @Ignore
    public String toString() {
        return "<ALARM "+alarm+"\nALARMLIST "+alarmList+"\nACTIVITY "+activity+"\nAGENDA"+agenda+">";
    }
}
