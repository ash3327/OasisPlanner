package com.aurora.oasisplanner.data.tags;

import java.util.regex.Pattern;

public class GroupType {
    public enum Type {gap, group, doc};

    private static String SEP = "$$";

    public Type type;
    public int i;

    public GroupType(Type type, int i) {
        this.type = type;
        this.i = i;
    }
    public static GroupType valueOf(String str) {
        String[] li = str.split(Pattern.quote(SEP));
        return new GroupType(Type.valueOf(li[0]), Integer.parseInt(li[1]));
    }
    public String toString() {
        return type.name()+SEP+i;
    }
}
