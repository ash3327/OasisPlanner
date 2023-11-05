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
    public static int WEEKDAY_CODES[] = new int[]{
            0b0, 0b1, 0b10, 0b100, 0b1000, 0b10000, 0b100000, 0b1000000
    };

    @Ignore
    public boolean visible = true;

    public _SubPeriod() {}

   @Ignore
    public void clearAllIds() {
        periodId = -1;
    }
    public void setPeriodId(long periodId) {
        clearAllIds();
        this.periodId = periodId;
    }

    @Ignore
    public boolean contains(LocalDateTime dt) {
        if (dt.isBefore(start) || dt.isAfter(end)) return false;                // contained in range
        return (WEEKDAY_CODES[dt.getDayOfWeek().getValue()] & weekdays) != 0;   // same weekday
    }

    @Ignore
    public String toString() {
        return " [_SubPeriod : "+id+" : "+start+" ~ "+end+" for "+weekdays+"] ";
    }
}
