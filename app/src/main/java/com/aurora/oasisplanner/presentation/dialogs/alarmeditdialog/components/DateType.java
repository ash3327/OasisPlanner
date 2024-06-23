package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components;

import androidx.annotation.NonNull;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum DateType {
    minutes, hours, days, weeks, months;

    private static String[] dateTypeStrings = Resources.getStringArr(R.array.date_types);

    @NonNull
    @Override
    public String toString() {
        if (dateTypeStrings == null) dateTypeStrings = Resources.getStringArr(R.array.date_types);
        return dateTypeStrings[ordinal()];
    }

    public boolean hasTime() {
        switch (this) {
            case minutes:
            case hours:
                return false;
            default:
                return true;
        }
    }

    private static final TemporalUnit[] units = new TemporalUnit[]{
            ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS,
            ChronoUnit.WEEKS, ChronoUnit.MONTHS
    };

    public TemporalUnit getTemporalUnit() {
        return units[ordinal()];
    }
}
