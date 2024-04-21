package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.RenameColumn;

import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;

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
    @ColumnInfo(name = "alarmId")
    public long id;
    @ColumnInfo(name = "alarmDatetime")
    public LocalDateTime datetime;
    @ColumnInfo(name = "alarmDuration")
    public Duration duration;
    @ColumnInfo(name = "alarmDate")
    public LocalDate date;
    @ColumnInfo(name = "alarmType")
    public AlarmType type;
    @ColumnInfo(name = "alarmImportance")
    public Importance importance;
    @ColumnInfo(name = "agendaId")
    public long agendaId = -1;
    @ColumnInfo(name = "alarmListId")
    public long alarmListId;
    @ColumnInfo(name = "activityId", defaultValue = "-1")
    public long activityId;
    @ColumnInfo(name = "alarmArgs")
    public Map<String,String> args = new HashMap<>();

    @Ignore
    boolean isSubalarm = false;
    public boolean isSubalarm() {return isSubalarm;}

    {
        setAlarmData(AlarmType.notif, Importance.regular);
    }

    @Ignore
    public boolean visible = true;

    public _Alarm() {}

    public _Alarm setAlarmData(AlarmType type, Importance importance) {
        this.type = type;
        this.importance = importance;
        return this;
    }
    public _Alarm setDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.datetime = date.atTime(time).withSecond(0);
        return this;
    }
    public _Alarm setDateTime(LocalDateTime ldt) {
        this.date = ldt.toLocalDate();
        this.datetime = ldt.withSecond(0);
        return this;
    }

    @Override
    public String toString() {
        return "<"+id+","+datetime+":"+date+">";
    }

    @Ignore
    public String getArg(ArgType key) {
        if (args == null || !args.containsKey(key.name()))
            return null;
        return args.get(key.name());
    }

    @Ignore
    public Map<String, String> getArgs() {
        if (args == null)
            args = new HashMap<>();
        return args;
    }

    @Ignore
    public void putArgs(ArgType key, SpannableStringBuilder sb) {
        if (args == null) args = new HashMap<>();
        args.put(key.name(), new Converters().spannableToString(sb));
    }
    @Ignore
    public void putArgs(ArgType key, String s) {
        if (args == null) args = new HashMap<>();
        args.put(key.name(), s);
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putLong("dateTime", converter.datetimeToTimestamp(datetime));
        extras.putLong("date", converter.dateToTimestamp(date));
        extras.putString("type", type.name());
        extras.putString("importance", importance.name());
        extras.putLong("agendaId", agendaId);
        extras.putLong("alarmListId", alarmListId);
        extras.putLong("activityId", activityId);
        extras.putSerializable("args", (Serializable) args);
        extras.putBoolean("isSubAlarm", isSubalarm);

        return extras;
    }

    @Ignore
    public static void unpack(_Alarm alarm, Bundle extras) {
        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.datetime = converter.datetimeFromTimestamp(extras.getLong("dateTime"));
        alarm.date = converter.dateFromTimestamp(extras.getLong("date"));
        alarm.type = AlarmType.valueOf(extras.getString("type"));
        alarm.importance = Importance.valueOf(extras.getString("importance"));
        alarm.agendaId = extras.getLong("agendaId");
        alarm.alarmListId = extras.getLong("alarmListId");
        alarm.activityId = extras.getLong("activityId");
        alarm.args = (Map<String,String>) extras.getSerializable("args");
        alarm.isSubalarm = extras.getBoolean("isSubAlarm");
    }
    @Ignore
    public static _Alarm unpackContents(Bundle extras) {
        _Alarm alarm = new _Alarm();

        unpack(alarm, extras);

        return alarm;
    }

    @Ignore
    public LocalDateTime getParentDatetime() {
        assert isSubalarm();
        try {
            return new Converters().datetimeFromTimestamp(Long.parseLong(getArg(ArgType.PARENT_TIME)));
        } catch (Exception e) {
            return null;
        }
    }

    public enum ArgType {
        PARENT_TIME,
        STATE;       // STATE: include options: {UNFINISHED, FINISHED}

        public String getDefault() {
            switch (this) {
                case STATE:
                    return "UNFINISHED";
            }
            return null;
        }
    }
}
