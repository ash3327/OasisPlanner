package com.aurora.oasisplanner.data.model.entities;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "_SubAlarm")
public class _SubAlarm extends _Alarm {
    @Ignore
    public static List<_SubAlarm> generateSubAlarms(AlarmList alarmList) {
        ArrayList<_SubAlarm> list = new ArrayList<>();
        for (_Alarm alarm : alarmList.alarms) {
            if (alarm.visible)
                list.addAll(generateSubAlarms(alarm,
                        alarmList.alarmList.getArg(TagType.ALARM.name())));
        }
        return list;
    }

    @Ignore
    public static List<_SubAlarm> generateSubAlarms(_Alarm alarm, SpannableStringBuilder subAlarmArg) {
        ArrayList<_SubAlarm> list = new ArrayList<>();
        if (subAlarmArg == null)
            return list;
        NotifType notifType = new NotifType(subAlarmArg.toString());
        _SubAlarm subAlarm = new _SubAlarm();
        LocalDateTime ldt = notifType.translate(alarm.datetime);
        subAlarm.setDateTime(ldt);
        list.add(subAlarm);
        return list;
    }

    //LATER: TODO: Insert subalarms; Remove all subalarms related to the particular AlarmList.
}
