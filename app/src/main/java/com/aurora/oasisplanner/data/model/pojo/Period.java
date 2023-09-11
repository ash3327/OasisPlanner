package com.aurora.oasisplanner.data.model.pojo;

import android.text.SpannableStringBuilder;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Period;
import com.aurora.oasisplanner.data.model.entities._Periods;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Period {
    @Embedded
    public _Periods period;
    // TODO: when the Period is updated, this should also be updated (bounds)

    @Relation(parentColumn = "id", entityColumn = "parentPeriodId")
    public List<_Period> periods = new ArrayList<>();

    @Ignore
    public boolean visible = true;
    @Ignore
    public SpannableStringBuilder contents;

    public Period(){}
}
