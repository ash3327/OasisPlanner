package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.RenameColumn;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.NotifType;
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
public class _Event extends __Item {

    @Ignore
    public boolean visible = true;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "eventId")
    public long id;
    @ColumnInfo(name = "eventTitle", defaultValue = "")
    public String title;
    @ColumnInfo(name = "eventDates")
    public List<LocalDate> dates;
    @ColumnInfo(name = "eventTime")
    public LocalTime time;
    @ColumnInfo(name = "eventType")
    public AlarmType type;
    @ColumnInfo(name = "eventImportance")
    public Importance importance;
    public long activityId;
    public long agendaId;
    @ColumnInfo(name = "eventI")
    public int i = -1;
    @ColumnInfo(name = "eventArgs")
    public Map<String,String> args = new HashMap<>();

    @Ignore
    private Event associates = null;
    @Ignore
    public String activityDescr = null;

    public _Event(){}

    @Ignore
    public _Event(AlarmType type, Importance importance) {
        this.type = type;
        this.importance = importance;
        this.dates = new ArrayList<>();
    }

    @Ignore
    public _Event putDates(LocalDate... dates) {
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
    public String getArgSpannableStr(TagType key) {
        SpannableStringBuilder ssb = getArgSpannable(key);
        return ssb != null ? ssb.toString() : null;
    }
    @Ignore
    public SpannableStringBuilder getArgSpannable(TagType key) {
        if (args == null || !args.containsKey(key.name()))
            return null;
        return new Converters().spannableFromString(args.get(key.name()));
    }
    @Ignore
    public String getArg(TagType key) {
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
    public void putArgs(String key, String sb) {
        if (args == null) args = new HashMap<>();
        args.put(key, new Converters().spannableToString(sb));
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
    public _Event setI(int i) {
        this.i = i;
        return this;
    }

    @Ignore
    public String getTitle() {
        if (title == null || title.isEmpty())
            title = getArg(TagType.DESCR);
        return title;
    }

    @Ignore
    public void setTitle(CharSequence ssb) {
        title = ssb.toString();
        putArgs(TagType.DESCR.name(), new SpannableStringBuilder(ssb));
    }
    @Ignore
    public void setTitle(String s) {
        title = s;
        putArgs(TagType.DESCR.name(), new SpannableStringBuilder(s));
    }

    @Ignore
    public String getTagsString() {
        return getArgSpannableStr(TagType.TAGS);
    }

    // INFO: GET ARGS:
    public SpannableStringBuilder getLoc() {
        SpannableStringBuilder out = getArgSpannable(TagType.LOC);
        return out;
    }
    public NotifType getNotifType() {
        String val = getArgSpannableStr(TagType.ALARM);
        return val != null ? new NotifType(val) : null;
    }


    @Ignore
    public boolean hasAssociates() {
        return associates != null;
    }

    /** Not Thread Safe. Wrap this function in executor.submit(). */
    @Ignore
    public Event getAssociates() {
        if (!hasAssociates())
            setAndWaitAssociates(); // not thread safe.
        return associates;
    }
    @Ignore
    public void setAssociates(Event associates) {
        associates.alarmList = this;
        this.associates = associates;
    }
    @Ignore
    private void setAndWaitAssociates() {
        try {
            Event associate = AppModule.retrieveEventUseCases().getWithChild(id).get();
            if (associate == null)
                associate = new Event(type, importance).putDates(time, dates);
            setAssociates(associate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public static _Event empty() {
        _Event alarmList = new _Event(AlarmType.notif, Importance.regular);
        alarmList.time = LocalTime.now();
        alarmList.dates = Collections.singletonList(LocalDate.now());
        return alarmList;
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putString("title", title);
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
    public static _Event unpackContents(Bundle extras) {
        _Event alarm = new _Event();

        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.title = extras.getString("title");
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
    private static long count = 0;
    @Ignore
    private long privateId = 0;
    public long getUniqueReference() {
        if (id != 0)
            return id;
        if (privateId != 0)
            return privateId;
        return privateId = --count;
    }

    @Ignore
    public String toString() {
        return "\n\t\t [ AlarmList : "+id+" : \n\t\t\t"+dates.toString()+"\n\t\t\t"+time.toString()+"\n\t\t ]";
    }
}
