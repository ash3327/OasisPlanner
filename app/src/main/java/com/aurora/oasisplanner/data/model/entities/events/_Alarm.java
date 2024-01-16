package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.__Entity;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.Styles;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Entity
public class _Alarm extends __Entity {
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

    @Ignore
    public boolean isSubalarm() {return false;}

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
        return "<"+id+","+getTitle()+","+getAgendaDescr()+","+getAlarmDescr()+","+datetime+":"+date+">";
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
        extras.putString("title", getTitle());
        extras.putString("agendaDescr", converter.spannableToString(getAgendaDescr()));
        extras.putString("alarmDescr", converter.spannableToString(getAlarmDescr()));
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

    // INFO: GET ARGS:
    public SpannableStringBuilder getLoc() {
        SpannableStringBuilder out = getArg(TagType.LOC.name());
        if (out == null || out.toString().isEmpty() || out.toString().equals("null"))
            return null;
        return out;
    }

    @Ignore
    public String getTagsString() {
        SpannableStringBuilder ssb = getArg(TagType.TAGS.name());
        return ssb==null ? null : ssb.toString();
    }

    @Ignore
    private _Activity activity = null;
    public _Activity getActivity() {
        if (activity == null) {
            try {
                activity = AppModule.retrieveActivityUseCases().get(activityId).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return activity;
    }

    @Ignore
    private _Agenda agenda = null;
    public _Agenda getAgenda() {
        if (agenda == null) {
            try {
                agenda = AppModule.retrieve_AgendaUseCases().get(agendaId).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return agenda;
    }
    
    public String getTitle() {
        //return getAgenda().title;
        return title;
    }
    public SpannableStringBuilder getAgendaDescr() {
        //return getAgenda().;
        return agendaDescr;
    }
    public SpannableStringBuilder getAlarmDescr() {
        //return getActivity().descr;
        return alarmDescr;
    }
}
