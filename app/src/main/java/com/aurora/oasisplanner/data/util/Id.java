package com.aurora.oasisplanner.data.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Id {
    private int id, defaultId;
    private ArrayList<IdObj> action = new ArrayList<>();
    public int key;
    public static HashMap<Integer, Id> idMap = new HashMap<>();
    public Id(int id, int key) {
        this.key= key;
        this.defaultId = id;
        if (idMap.containsKey(key))
            this.id = idMap.get(key).id;
        else this.id = this.defaultId;

        idMap.put(key, this);
    }
    public int getId(){ return id; }
    public boolean setId(int id) {
        boolean changed = this.id != id;
        if (changed)
            for (IdObj action : this.action)
                action.run(this.id, id);
        this.id = id;
        idMap.put(key, this);
        return changed;
    }
    public boolean toggleId(int id) {
        if (this.id == id) setId(defaultId);
        else setId(id);
        return true;
    }
    public boolean equals(int id2) {
        return id == id2;
    }
    public void observe(IdObj action) { observe(action, false); }
    public void observe(IdObj action, boolean act) {
        if (!this.action.contains(action))
            this.action.add(action);
        if (act)
            action.run(id, id);
    }

    public interface IdObj{
        void run(int oldId, int newId);
    }
}
