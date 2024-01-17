package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class _AlarmList {
    @Ignore
    public boolean visible = true;
    @PrimaryKey(autoGenerate = true)
    public long id;
    public List<LocalDate> dates;
    public LocalTime time;
    public AlarmType type;
    public Importance importance;
    public long activityId;
    public long agendaId;
    public int i = -1;
    public Map<String,String> args = new HashMap<>();

    @Ignore
    private AlarmList associates = null;

    public _AlarmList(){}

    @Ignore
    public _AlarmList(AlarmType type, Importance importance) {
        this.type = type;
        this.importance = importance;
        this.dates = new ArrayList<>();
    }

    @Ignore
    public _AlarmList putDates(LocalDate... dates) {
        this.dates = Arrays.stream(dates).collect(Collectors.toList());
        return this;
    }

    @Ignore
    public String getDateTime() {
        return DateTimesFormatter.getDateTime(dates, time);
    }

    @Ignore
    public LocalDateTime getNextDateTime() {
        for (LocalDate date : dates) {
            LocalDateTime dt = date.atTime(time);
            if (!dt.isBefore(LocalDateTime.now()))
                return dt;
        }
        return null;
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
    public void removeKey(String key) {
        if (args == null) args = new HashMap<>();
        args.remove(key);
    }

    @Ignore
    public _AlarmList setI(int i) {
        this.i = i;
        return this;
    }

    @Ignore
    public String getTagsString() {
        SpannableStringBuilder ssb = getArg(TagType.TAGS.name());
        return ssb==null ? null : ssb.toString();
    }
    // INFO: GET ARGS:
    public SpannableStringBuilder getLoc() {
        SpannableStringBuilder out = getArg(TagType.LOC.name());
        if (out == null || out.toString().isEmpty() || out.toString().equals("null"))
            return null;
        return out;
    }


    @Ignore
    public boolean hasAssociates() {
        return associates != null;
    }

    /** Not Thread Safe. Wrap this function in executor.submit(). */
    @Ignore
    public AlarmList getAssociates() {
        if (!hasAssociates())
            setAndWaitAssociates(); // not thread safe.
        return associates;
    }
    @Ignore
    public void setAssociates(AlarmList associates) {
        associates.alarmList = this;
        this.associates = associates;
    }
    @Ignore
    private void setAndWaitAssociates() {
        try {
            AlarmList associate = AppModule.retrieveEventUseCases().getWithChild(id).get();
            if (associate == null)
                associate = new AlarmList(type, importance).putDates(time, dates);
            setAssociates(associate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public static _AlarmList empty() {
        _AlarmList alarmList = new _AlarmList(AlarmType.notif, Importance.regular);
        alarmList.time = LocalTime.now();
        alarmList.dates = Collections.singletonList(LocalDate.now());
        return alarmList;
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putString("dates", converter.localDatesToString(dates));
        extras.putLong("time", converter.localTimeToTimestamp(time));
        extras.putString("type", type.name());
        extras.putString("importance", importance.name());
        extras.putLong("agendaId", agendaId);
        extras.putLong("activityId", activityId);
        extras.putSerializable("args", (Serializable) args);

        return extras;
    }

    @Ignore
    public static _AlarmList unpackContents(Bundle extras) {
        _AlarmList alarm = new _AlarmList();

        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.dates = converter.localDatesFromString(extras.getString("dates"));
        alarm.time = converter.localTimeFromTimestamp(extras.getLong("time"));
        alarm.type = AlarmType.valueOf(extras.getString("type"));
        alarm.importance = Importance.valueOf(extras.getString("importance"));
        alarm.agendaId = extras.getLong("agendaId");
        alarm.activityId = extras.getLong("activityId");
        alarm.args = (Map<String,String>) extras.getSerializable("args");

        return alarm;
    }

    @Ignore
    public String toString() {
        return "\n\t\t [ AlarmList : "+id+" : \n\t\t\t"+dates.toString()+"\n\t\t\t"+time.toString()+"\n\t\t ]";
    }
}