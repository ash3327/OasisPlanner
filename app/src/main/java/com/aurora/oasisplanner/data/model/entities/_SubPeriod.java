package com.aurora.oasisplanner.data.model.entities;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class _SubPeriod {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public LocalDateTime start, end;
    public int weekdays;
    public long periodId;

    @Ignore
    public boolean visible = true;

    public _SubPeriod() {}

    public void clearAllIds() {
        periodId = -1;
    }
    public void setPeriodId(long periodId) {
        clearAllIds();
        this.periodId = periodId;
    }

    @Ignore
    public String toString() {
        return " [_SubPeriod : "+id+" : "+start+" ~ "+end+" for "+weekdays+"] ";
    }
}
