package com.aurora.oasisplanner.data.tags;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.DateType;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NotifType {
    public int val;
    public DateType dateType;
    public int hour, minute;
    public boolean hasTime;

    public NotifType(int val, DateType dateType) {
        this.val = val;
        this.dateType = dateType;
        this.hasTime = false;
    }
    public NotifType(int val, DateType dateType, int hour, int minute) {
        this.val = val;
        this.dateType = dateType;
        this.hasTime = true;
        this.hour = hour;
        this.minute = minute;
    }
    public NotifType(String encoded) {
        String[] res = encoded.split(":");
        assert res.length >= 2;
        this.val = Integer.parseInt(res[0]);
        this.dateType = DateType.valueOf(res[1]);
        if (res.length == 4) {
            hasTime = true;
            this.hour = Integer.parseInt(res[2]);
            this.minute = Integer.parseInt(res[3]);
        } else hasTime = false;
    }

    public static NotifType getDefault() {
        return new NotifType(30, DateType.minutes);
    }

    public static final String SEP = ";";
    public static List<NotifType> loadFromString(String notifString) {
        if (notifString == null)
            return new ArrayList<>();
        return Arrays.stream(notifString.split(SEP)).map(NotifType::new).collect(Collectors.toList());
    }
    public static String saveToString(List<NotifType> notifTypes) {
        return notifTypes.stream().map(NotifType::toString).collect(Collectors.joining(SEP));
    }
    public static String loadDescFrom(List<NotifType> notifTypes) {
        return notifTypes.stream().map(NotifType::getDescription).collect(Collectors.joining(", "));
    }

    public LocalDateTime translate(LocalDateTime ldt) {
        if (hasTime)
            ldt = ldt.withHour(hour).withMinute(minute).withSecond(0);
        ldt = ldt.minus(val, dateType.getTemporalUnit());
        return ldt;
    }

    public String toString() {
        if (hasTime)
            return val+":"+dateType.name()+":"+hour+":"+minute;
        else
            return val+":"+dateType.name();
    }
    public String getDescription() {
        if (hasTime)
            return String.format(Resources.getString(R.string.tag_time_full),
                    val,dateType.toString(),hour,minute);
        else
            return String.format(Resources.getString(R.string.tag_time_half),
                    val,dateType.toString());
    }
}
