package com.aurora.oasisplanner.data.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public long groupId;
    public long agendaId;
    public int i = -1;

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
    public String toString() {
        return "\n\t\t [ AlarmList : "+id+" : \n\t\t\t"+dates.toString()+"\n\t\t\t"+time.toString()+"\n\t\t ]";
    }
}
