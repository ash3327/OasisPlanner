package com.aurora.oasisplanner.data.model.entities.events;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.__Entity;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Entity
public class _AlarmList extends __Entity {
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
    public String toString() {
        return "\n\t\t [ AlarmList : "+id+" : \n\t\t\t"+dates.toString()+"\n\t\t\t"+time.toString()+"\n\t\t ]";
    }
}
