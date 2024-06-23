package com.aurora.oasisplanner.presentation.widgets.multidatepicker.data;

import java.time.LocalDate;

public class DateRange {
    public LocalDate mStart, mEnd;
    private DateRange.Updating changingStartEndPointerState = DateRange.Updating.notUpdating;

    public DateRange setPoint(LocalDate point) {
        changingStartEndPointerState = DateRange.Updating.notUpdating;
        mStart = mEnd = point;
        return this;
    }

    public DateRange setRange(LocalDate start, LocalDate end) {
        changingStartEndPointerState = DateRange.Updating.notUpdating;
        mStart = start;
        mEnd = end;
        return this;
    }

    public DateRange setToday() {
        changingStartEndPointerState = DateRange.Updating.notUpdating;
        mStart = LocalDate.now();
        mEnd = mStart.plusDays(1);
        return this;
    }

    public void changeRangeBy(LocalDate pivot) {
        if (pivot.isBefore(mStart) || pivot.equals(mStart))
            changingStartEndPointerState = DateRange.Updating.updatingStart;
        else if (pivot.isAfter(mEnd) || pivot.equals(mEnd))
            changingStartEndPointerState = DateRange.Updating.updatingEnd;
        switch (changingStartEndPointerState) {
            case updatingStart:
                mStart = pivot;
                break;
            case updatingEnd:
                mEnd = pivot;
                break;
        }
    }

    public boolean contains(LocalDate date) {
        return date.equals(mStart) || date.equals(mEnd) || date.isAfter(mStart) && date.isBefore(mEnd);
    }

    enum Updating {
        notUpdating, updatingStart, updatingEnd
    }
}
