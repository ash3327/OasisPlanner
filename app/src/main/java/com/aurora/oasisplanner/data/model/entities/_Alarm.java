package com.aurora.oasisplanner.data.model.entities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.Styles;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class _Alarm {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title;
    public SpannableStringBuilder agendaDescr;
    public SpannableStringBuilder alarmDescr;
    public LocalDateTime datetime;
    public Duration duration;
    public LocalDate date;
    public AlarmType type;
    public Importance importance;
    public long agendaId = -1;
    public long alarmListId;
    @ColumnInfo(defaultValue = "-1")
    public long activityId;
    public Map<String,String> args = new HashMap<>();

    {
        setAgendaData("title", "agendaDescr", "alarmDescr");
        setAlarmData(AlarmType.notif, Importance.regular);
    }

    @Ignore
    public boolean visible = true;

    public _Alarm() {}

    public _Alarm setAgendaData(String title, CharSequence agendaDescr, CharSequence alarmDescr) {
        this.title = title;
        this.agendaDescr = new SpannableStringBuilder(agendaDescr);
        this.alarmDescr = new SpannableStringBuilder(alarmDescr);
        return this;
    }
    public _Alarm setAlarmData(AlarmType type, Importance importance) {
        this.type = type;
        this.importance = importance;
        return this;
    }
    public _Alarm setDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.datetime = date.atTime(time);
        return this;
    }
    public _Alarm setDateTime(LocalDateTime ldt) {
        this.date = ldt.toLocalDate();
        this.datetime = ldt;
        return this;
    }

    public static class InvalidAlarmException extends RuntimeException {
        public InvalidAlarmException(String message) {
            super(message);
        }
    }

    @Override
    public String toString() {
        return "<"+id+","+title+","+agendaDescr+","+alarmDescr+","+datetime+":"+date+">";
    }

    @Ignore
    public SpannableStringBuilder getContents(boolean inExpandedMode) {
        return inExpandedMode ?
                agendaDescr.toString().isEmpty() ?
                        new SpannableStringBuilder()
                                .append(alarmDescr) :
                        new SpannableStringBuilder()
                                .append(alarmDescr)
                                .append("\n\n")
                                .append(agendaDescr)
                : Styles.truncate(alarmDescr, 12);
    }

    @Ignore
    public SpannableStringBuilder getArg(String key) {
        if (args == null || !args.containsKey(key))
            return null;
        return new Converters().spannableFromString(args.get(key));
    }

    @Ignore
    public Map<String, String> getArgs() {
        if (args == null)
            args = new HashMap<>();
        return args;
    }

    @Ignore
    public void putArgs(String key, SpannableStringBuilder sb) {
        if (args == null) args = new HashMap<>();
        args.put(key, new Converters().spannableToString(sb));
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putString("title", title);
        extras.putString("agendaDescr", converter.spannableToString(agendaDescr));
        extras.putString("alarmDescr", converter.spannableToString(alarmDescr));
        extras.putLong("dateTime", converter.datetimeToTimestamp(datetime));
        extras.putLong("date", converter.dateToTimestamp(date));
        extras.putString("type", type.name());
        extras.putString("importance", importance.name());
        extras.putLong("agendaId", agendaId);
        extras.putLong("alarmListId", alarmListId);
        extras.putLong("activityId", activityId);
        extras.putSerializable("args", (Serializable) args);

        return extras;
    }

    @Ignore
    public static _Alarm unpackContents(Bundle extras) {
        _Alarm alarm = new _Alarm();

        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.title = extras.getString("title");
        alarm.agendaDescr = converter.spannableFromString(extras.getString("agendaDescr"));
        alarm.alarmDescr = converter.spannableFromString(extras.getString("alarmDescr"));
        alarm.datetime = converter.datetimeFromTimestamp(extras.getLong("dateTime"));
        alarm.date = converter.dateFromTimestamp(extras.getLong("date"));
        alarm.type = AlarmType.valueOf(extras.getString("type"));
        alarm.importance = Importance.valueOf(extras.getString("importance"));
        alarm.agendaId = extras.getLong("agendaId");
        alarm.alarmListId = extras.getLong("alarmListId");
        alarm.activityId = extras.getLong("activityId");
        alarm.args = (Map<String,String>) extras.getSerializable("args");

        return alarm;
    }
}
