package com.aurora.oasisplanner.data.model.pojo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._Agenda;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Group;
import com.aurora.oasisplanner.data.tags.AgendaType;
import com.aurora.oasisplanner.data.tags.GroupType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Agenda {
    @Embedded
    public _Agenda agenda;

    @Relation(parentColumn = "id", entityColumn = "agendaId", entity = _Group.class)
    public List<Group> groups = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "agendaId", entity = _Doc.class)
    public List<_Doc> docs = new ArrayList<>();

    @Ignore
    public List<Group> invisGroups = new ArrayList<>();
    @Ignore
    public List<_Doc> invisDocs = new ArrayList<>();

    public Agenda(){}

    @Ignore
    public Agenda(AgendaType type, String title) {
        this.agenda = new _Agenda(type, title);
    }

    @Ignore
    public Agenda putItems(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof Group) {
                agenda.types.add(new GroupType(GroupType.Type.group, groups.size()));
                groups.add((Group) obj);
            }
            if (obj instanceof _Doc) {
                agenda.types.add(new GroupType(GroupType.Type.doc, docs.size()));
                docs.add((_Doc) obj);
            }
        }
        return this;
    }

    public static Agenda empty() {
        return new Agenda(
                AgendaType.agenda,
                ""
        ).putItems(Group.empty());
    }

    @Ignore
    public List[] getObjList(boolean sort) {
        List<Object> list = new ArrayList<>();
        List<GroupType.Type> types = new ArrayList<>();

        if (sort) {
            groups.sort(Comparator.comparingInt(a -> a.group.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (GroupType gt : agenda.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case group:
                    obj = groups.get(gt.i);
                    visible = ((Group) obj).visible;
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
        for (Group gp : groups)
            if (!gp.visible) invisGroups.add(gp);
        for (_Doc doc : docs)
            if (!doc.visible) invisDocs.add(doc);

        return new List[]{list, types};
    }

    @Ignore
    public void update() {
        List<Object> list = getObjList(false)[0];

        groups = new ArrayList<>();
        docs = new ArrayList<>();
        agenda.types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof Group) {
                agenda.types.add(new GroupType(GroupType.Type.group, groups.size()));
                groups.add(((Group) obj).setI(i));
            }
            if (obj instanceof _Doc) {
                agenda.types.add(new GroupType(GroupType.Type.doc, docs.size()));
                docs.add(((_Doc) obj).setI(i));
            }
            i++;
        }
    }

    @Ignore
    public String toString() {
        return "\n [ Agenda : "+agenda.id+" : "+agenda.title+" : "+getObjList(false)[0]+"\n delete gps: "+invisGroups+"\n delete docs: "+invisDocs+"\n ]";
    }
}
