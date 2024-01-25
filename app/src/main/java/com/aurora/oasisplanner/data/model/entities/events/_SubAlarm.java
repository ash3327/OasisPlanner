package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "_SubAlarm")
public class _SubAlarm extends _Alarm {

    {
        isSubalarm = true;
    }

    @Ignore
    public static List<_SubAlarm> generateSubAlarms(Event event) {
        ArrayList<_SubAlarm> list = new ArrayList<>();
        for (_Alarm alarm : event.alarms) {
            if (alarm.visible)
                list.addAll(generateSubAlarms(alarm,
                        event.alarmList.getArg(TagType.ALARM.name())));
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

    @Ignore
    public static _SubAlarm unpackContents(Bundle extras) {
        _SubAlarm alarm = new _SubAlarm();

        unpack(alarm, extras);

        return alarm;
    }

    //LATER: TODO: Insert subalarms; Remove all subalarms related to the particular AlarmList.
}
