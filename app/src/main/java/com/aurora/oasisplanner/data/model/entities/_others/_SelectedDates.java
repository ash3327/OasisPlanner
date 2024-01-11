package com.aurora.oasisplanner.data.model.entities._others;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class _SelectedDates {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public List<LocalDate> dates;
    public long periodId;

    @Ignore
    public boolean visible = true;

    public _SelectedDates() {
        this.dates = new ArrayList<>();
    }

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
        return dates.contains(dt.toLocalDate());
    }

    @Ignore
    public String toString() {
        return " [_SubPeriod : "+id+" : "+dates+"] ";
    }
}
