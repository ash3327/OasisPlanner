package com.aurora.oasisplanner.data.model.pojo._others;

import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._others._SelectedDates;
import com.aurora.oasisplanner.data.model.entities._others._SubPeriod;

import java.util.ArrayList;
import java.util.List;

public class Period {
    @Relation(
        parentColumn = "periodId", entityColumn = "periodId"
    )
    public List<_SubPeriod> subPeriods = new ArrayList<>();
    public _SelectedDates   included = new _SelectedDates(),
                            excluded = new _SelectedDates();
}
