package com.aurora.oasisplanner.data.model.pojo;

import static com.aurora.oasisplanner.data.tags.ActivityType.Type.doc;
import static com.aurora.oasisplanner.data.tags.ActivityType.Type.loc;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.SectionItemAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Activity {
    @Embedded
    public _Activity activity;

    @Relation(parentColumn = "id", entityColumn = "groupId", entity = _AlarmList.class)
    public List<AlarmList> alarmList = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "groupId", entity = _Doc.class)
    public List<_Doc> docs = new ArrayList<>();

    @Ignore
    public List<AlarmList> invisGroups = new ArrayList<>();
    @Ignore
    public List<_Doc> invisDocs = new ArrayList<>();

    @Ignore
    public boolean visible = true;

    /** This constructor is not for programmer access. Please use Activity(null) instead. */
    public Activity(){}

    /** Please always use this constructor with content NULL. */
    @Ignore
    public Activity(Object obj) {
        this.activity = new _Activity();
    }

    @Ignore
    public Activity putItems(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof AlarmList) {
                activity.types.add(new ActivityType(ActivityType.Type.activity, alarmList.size()));
                alarmList.add((AlarmList) obj);
            }
            if (obj instanceof _Doc) {
                activity.types.add(new ActivityType(ActivityType.Type.doc, docs.size()));
                docs.add((_Doc) obj);
            }
        }
        return this;
    }

    @Ignore
    public static Activity empty() {
        return new Activity(null).putItems(
                _Doc.empty()
        );
    }

    @Ignore
    public _Doc getLoc(AlarmList aL) {
        _Doc out = null;
        List[] objList = getObjList(false);
        int i = 0;
        for (Object obj : objList[0]) {
            if (obj instanceof _Doc && objList[1].get(i) == loc)
                out = (_Doc) obj;
            if (Objects.equals(obj, aL))
                break;
            i++;
        }
        return out;
    }
    /** Returns Object[3]: {alarmList: AlarmList, firstDateTime: LocalDateTime}*/
    @Ignore
    public Object[] getFirstAlarmList() {
        AlarmList aL = alarmList.get(0);
        LocalDateTime dt = aL.alarmList.getNextDateTime();
        for (AlarmList al : alarmList) {
            LocalDateTime aldt = al.alarmList.getNextDateTime();;
            if (aldt.isBefore(dt)) {
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
            alarmList.sort(Comparator.comparingInt(a -> a.alarmList.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (ActivityType gt : activity.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case activity:
                    obj = alarmList.get(gt.i);
                    visible = ((AlarmList) obj).visible;
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
        for (AlarmList gp : alarmList)
            if (!gp.visible) invisGroups.add(gp);
        for (_Doc doc : docs)
            if (!doc.visible) invisDocs.add(doc);

        return new List[]{list, types};
    }

    @Ignore
    public void update() {
        List<Object> list = getObjList(false)[0];

        alarmList = new ArrayList<>();
        docs = new ArrayList<>();
        ArrayList<ActivityType> types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof AlarmList) {
                types.add(new ActivityType(ActivityType.Type.activity, alarmList.size()));
                alarmList.add(((AlarmList) obj).setI(i));
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
