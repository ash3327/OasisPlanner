package com.aurora.oasisplanner.data.model.pojo.events;

import android.text.SpannableStringBuilder;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.tags.ActivityType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Activity {
    @Embedded
    public _Activity activity;

    @Relation(parentColumn = "id", entityColumn = "activityId", entity = _AlarmList.class)
    public List<_AlarmList> alarmLists = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "groupId", entity = _Doc.class)
    public List<_Doc> docs = new ArrayList<>();

    @Ignore
    public List<_AlarmList> invisGroups = new ArrayList<>();
    @Ignore
    public List<_Doc> invisDocs = new ArrayList<>();

    @Ignore
    public boolean visible = true;

    /** This constructor is not for programmer access. Please use Activity(null) instead. */
    public Activity(){}

    @Ignore
    public Activity(String descr) {
        this.activity = new _Activity();
        this.activity.descr = new SpannableStringBuilder(descr);
    }

    /** Please always use this constructor with content NULL. */
    @Ignore
    public Activity(Object obj) {
        this.activity = new _Activity();
    }

    @Ignore
    public static Activity empty() {
        return new Activity(null);
    }

    /** Returns Object[3]: {_AlarmList: _AlarmList, firstDateTime: LocalDateTime}*/
    @Ignore
    public Object[] getFirstAlarmList() {
        _AlarmList aL = alarmLists.get(0);
        LocalDateTime dt = aL.getNextDateTime();
        for (_AlarmList al : alarmLists) {
            LocalDateTime aldt = al.getNextDateTime();
            if (aldt != null && (dt == null || aldt.isBefore(dt))) {
                aL = al;
                dt = aldt;
            }
        }
        return new Object[]{aL, dt};
    }

    /** output format: List[2]: {objlist: List<Object>, types: List<ActivityType.Type>}*/
    @Ignore
    public List[] getObjList(boolean sort) {
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        if (sort) {
            alarmLists.sort(Comparator.comparingInt(a -> a.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (ActivityType gt : activity.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case activity:
                    obj = alarmLists.get(gt.i);
                    visible = ((_AlarmList) obj).visible;
                    break;
                case doc:
                case loc:
                    obj = docs.get(gt.i);
                    visible = ((_Doc) obj).visible;
                    break;
            }
            if (visible && obj != null) {// && gt.type != doc) {
                list.add(obj);
                types.add(gt.type);
            }
        }
        for (_AlarmList gp : alarmLists)
            if (!gp.visible) invisGroups.add(gp);
        for (_Doc doc : docs)
            if (!doc.visible) invisDocs.add(doc);

        return new List[]{list, types};
    }

    @Ignore
    public void update() {
        List<Object> list = getObjList(false)[0];

        alarmLists = new ArrayList<>();
        docs = new ArrayList<>();
        ArrayList<ActivityType> types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof _AlarmList) {
                types.add(new ActivityType(ActivityType.Type.activity, alarmLists.size()));
                alarmLists.add(((_AlarmList) obj).setI(i));
            }
            if (obj instanceof _Doc) {
                _Doc doc = (_Doc) obj;
                if (activity.types.get(i).type == ActivityType.Type.loc)
                    types.add(new ActivityType(ActivityType.Type.loc, docs.size()));
                else
                    types.add(new ActivityType(ActivityType.Type.doc, docs.size()));
                docs.add(((_Doc) obj).setI(i));
            }
            i++;
        }
        activity.types.clear();
        activity.types.addAll(types);
    }

    public Activity setI(int i) {
        this.activity.i = i;
        return this;
    }

    @Ignore
    public String toString() {
        return "\n\t [ Activity : "+activity.id+" : "+getObjList(false)[0]+"\n\t delete gps: "+invisGroups+"\n\t delete docs: "+invisDocs+"\n\t ]";
    }
}
