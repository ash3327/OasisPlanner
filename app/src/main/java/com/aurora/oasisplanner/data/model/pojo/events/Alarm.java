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
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Alarm {
    @Embedded
    public _Alarm alarm;
    
    @Relation(parentColumn = "agendaId", entityColumn = "id", entity = _Agenda.class)
    public _Agenda agenda;
    @Relation(parentColumn = "activityId", entityColumn = "id", entity = _Activity.class)
    public _Activity activity;
    @Relation(parentColumn = "alarmListId", entityColumn = "id", entity = _Event.class)
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
        return getAgenda().title;
    }
    public SpannableStringBuilder getAgendaDescr() {
        return new SpannableStringBuilder(getTitle());
    }
    public SpannableStringBuilder getActivityDescr() {
        return getActivity().descr;
    }
    public String getEventDescr() {
        return getEvent().getTitle();
    }
    @Ignore
    public SpannableStringBuilder getContents(boolean inExpandedMode) {
        SpannableStringBuilder out = new SpannableStringBuilder(), temp;
        String SEP = inExpandedMode ? "\n\n" : " • ", temp2;

        if (!Styles.isEmpty(temp = getActivityDescr()))  out.append(temp).append(SEP);
        if (!Styles.isEmpty(temp2 = getEventDescr()))     out.append(temp2).append(SEP);

        if (!Styles.isEmpty(temp = getAgendaDescr()) && inExpandedMode)
            out.append(temp).append(SEP);

        int len = out.length();
        out = out.delete(len-SEP.length(), len);

        return inExpandedMode ? out : Styles.truncate(out, 12);
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
    public long getAlarmListId() {
        return getEvent().id;
    }
    public long getAlarmId() {
        return getAlarm().id;
    }
    public long getEncodedId() {
        return getAlarmId() * 2 + (getAlarm().isSubalarm()?0:1);
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        extras.putBundle("alarm", getAlarm().packContents());
        extras.putBundle("alarmList", getEvent().packContents());
        extras.putBundle("activity", getActivity().packContents());
        extras.putBundle("agenda", getAgenda().packContents());

        return extras;
    }

    @Ignore
    public static void unpackExceptAlarm(Alarm alarm, Bundle extras) {
        alarm.setEvent(_Event.unpackContents(extras.getBundle("alarmList")));
        alarm.setActivity(_Activity.unpackContents(extras.getBundle("activity")));
        alarm.setAgenda(_Agenda.unpackContents(extras.getBundle("agenda")));
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
        return "<ALARM "+alarm+"\nALARMLIST "+ event +"\nACTIVITY "+activity+"\nAGENDA"+agenda+">";
    }
}
