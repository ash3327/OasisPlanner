package com.aurora.oasisplanner.data.model.pojo.events;

import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Alarm {
    @Embedded
    public _Alarm alarm;
    
    @Relation(parentColumn = "agendaId", entityColumn = "agendaId", entity = _Agenda.class)
    public _Agenda agenda;
    @Relation(parentColumn = "activityId", entityColumn = "activityId", entity = _Activity.class)
    public _Activity activity;
    @Relation(parentColumn = "alarmListId", entityColumn = "eventId", entity = _Event.class)
    public _Event event;

    public Alarm(){}

    public _Alarm getAlarm() { return alarm; }
    public _Agenda getAgenda() { return agenda; }
    public _Activity getActivity() { return activity; }
    public _Event getEvent() { return event; }
    public void setAlarm(_Alarm alarm) { this.alarm = alarm; }
    public void setAgenda(_Agenda agenda) { this.agenda = agenda; }
    public void setActivity(_Activity activity) { this.activity = activity; }
    public void setEvent(_Event event) { this.event = event; }

    public AlarmType getType() {
        return getEvent().type;
    }
    public Importance getImportance() {
        return getEvent().importance;
    }
    public LocalDateTime getDateTime() {
        return getAlarm().datetime;
    }
    public LocalDate getDate() {
        return getAlarm().date;
    }

    public String getTitle() {
        return getEventDescr();
    }
    public String getAgendaDescr() {
        return getAgenda().title;
    }
    public SpannableStringBuilder getActivityDescr() {
        return getActivity().title;
    }
    public String getEventDescr() {
        return getEvent().getTitle();
    }
    @Ignore
    public SpannableStringBuilder getContents(boolean inExpandedMode) {
        return getContentStringFrom(
                getAgendaDescr(),
                getActivityDescr(),
                getEventDescr(),
                inExpandedMode
        );
    }
    @Ignore
    public static SpannableStringBuilder getContentStringFrom(
        String agendaDescr, SpannableStringBuilder activityDescr,
        String eventDescr, boolean inExpandedMode
    ) {
        SpannableStringBuilder out = new SpannableStringBuilder(), temp;
        String SEP = " • ";//inExpandedMode ? "\n" : " • ";
        String temp2;

        if (!Styles.isEmpty(temp2 = agendaDescr))  out.append(temp2).append(SEP);
        if (!Styles.isEmpty(temp = activityDescr)) out.append(temp).append(SEP);

        if (!Styles.isEmpty(temp2 = eventDescr) && inExpandedMode)
            out.append(temp2).append(SEP);

        int len = out.length();
        if (len > SEP.length())
            out = out.delete(len-SEP.length(), len);

        return inExpandedMode ? out : Styles.truncate(out, 30);
    }
    @Ignore
    public String getTagsString() {
        String out = "";
        out += getEvent().getTagsString();
        return out.isEmpty() || out.equals(null + "") ? null : out;
    }
    @Ignore
    public SpannableStringBuilder getLoc() {
        return getEvent().getLoc();
    }

    public long getAgendaId() {
        return getAgenda().id;
    }
    public long getActivityId() {
        return getActivity().id;
    }
    public long getEventId() {
        return getEvent().id;
    }
    public long getAlarmId() {
        return getAlarm().id;
    }
    public long getEncodedId() {
        return getAlarmId() * 2 + (getAlarm().isSubalarm()?0:1);
    }
    public String getArg(_Alarm.ArgType argType) {return getAlarm().getArg(argType);}
    @Ignore
    public String getArgDefault(_Alarm.ArgType argType) {
        String val = getArg(argType);
        if (val == null) {
            getAlarm().putArgs(argType, argType.getDefault());
            return getArg(argType);
        }
        return val;
    }
    public void putArg(_Alarm.ArgType argType, String val) {getAlarm().putArgs(argType, val);}

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        extras.putBundle("alarm", getAlarm().packContents());
        extras.putBundle("alarmList", getEvent().packContents());
        if (getActivity() != null) {
            extras.putBundle("activity", getActivity().packContents());
            extras.putBundle("agenda", getAgenda().packContents());
        }
        extras.putBoolean("hasActivity", getActivity() != null);

        return extras;
    }

    @Ignore
    public static void unpackExceptAlarm(Alarm alarm, Bundle extras) {
        alarm.setEvent(_Event.unpackContents(extras.getBundle("alarmList")));
        if (extras.getBoolean("hasActivity")) {
            alarm.setActivity(_Activity.unpackContents(extras.getBundle("activity")));
            alarm.setAgenda(_Agenda.unpackContents(extras.getBundle("agenda")));
        }
    }
    @Ignore
    public static Alarm unpackContents(Bundle extras) {
        Alarm alarm = new Alarm();

        alarm.setAlarm(_Alarm.unpackContents(extras.getBundle("alarm")));
        unpackExceptAlarm(alarm, extras);

        return alarm;
    }

    @Ignore
    public String toString() {
        return "\n\n<ALARM "+getAlarmId()+"-"+getEventId()+"::"+alarm+"\nALARMLIST "+ event +"\nACTIVITY "+activity+"\nAGENDA"+agenda+">";
    }
}
