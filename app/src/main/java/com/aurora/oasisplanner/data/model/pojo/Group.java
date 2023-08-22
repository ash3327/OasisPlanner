package com.aurora.oasisplanner.data.model.pojo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Group;
import com.aurora.oasisplanner.data.tags.GroupType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Group {
    @Embedded
    public _Group group;

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

    public Group(){}

    /** Please always use this constructor. */
    @Ignore
    public Group(Object obj) {
        this.group = new _Group();
    }

    @Ignore
    public Group putItems(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof AlarmList) {
                group.types.add(new GroupType(GroupType.Type.group, alarmList.size()));
                alarmList.add((AlarmList) obj);
            }
            if (obj instanceof _Doc) {
                group.types.add(new GroupType(GroupType.Type.doc, docs.size()));
                docs.add((_Doc) obj);
            }
        }
        return this;
    }

    @Ignore
    public static Group empty() {
        return new Group(null).putItems(
                _Doc.empty()
        );
    }

    @Ignore
    public List[] getObjList(boolean sort) {
        List<Object> list = new ArrayList<>();
        List<GroupType.Type> types = new ArrayList<>();

        if (sort) {
            alarmList.sort(Comparator.comparingInt(a -> a.alarmList.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (GroupType gt : group.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case group:
                    obj = alarmList.get(gt.i);
                    visible = ((AlarmList) obj).visible;
                    break;
                case doc:
                    obj = docs.get(gt.i);
                    visible = ((_Doc) obj).visible;
                    break;
            }
            if (visible && obj != null) {
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
        group.types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof AlarmList) {
                group.types.add(new GroupType(GroupType.Type.group, alarmList.size()));
                alarmList.add(((AlarmList) obj).setI(i));
            }
            if (obj instanceof _Doc) {
                group.types.add(new GroupType(GroupType.Type.doc, docs.size()));
                docs.add(((_Doc) obj).setI(i));
            }
            i++;
        }
    }

    public Group setI(int i) {
        this.group.i = i;
        return this;
    }

    @Ignore
    public String toString() {
        return "\n\t [ Group : "+group.id+" : "+getObjList(false)[0]+"\n\t delete gps: "+invisGroups+"\n\t delete docs: "+invisDocs+"\n\t ]";
    }
}
