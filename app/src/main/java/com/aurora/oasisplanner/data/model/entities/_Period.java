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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class _Period {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name; //Not used
    public LocalDate fromDate = null, toDate = null;

    @ColumnInfo(defaultValue = "-1")
    public long parentPeriodId = -1;

    @Ignore
    public boolean visible = true;

    public _Period() {}

    @Override
    public String toString() {
        return "<"+id+","+ parentPeriodId +","+fromDate+"~"+toDate+">";
    }
}
