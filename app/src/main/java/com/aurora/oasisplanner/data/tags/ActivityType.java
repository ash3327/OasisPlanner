package com.aurora.oasisplanner.data.tags;

import java.util.regex.Pattern;

public class ActivityType {
    public enum Type {gap, activity, doc};

    private static String SEP = "$$";

    public Type type;
    public int i;

    public ActivityType(Type type, int i) {
        this.type = type;
        this.i = i;
    }
    public static ActivityType valueOf(String str) {
        String[] li = str.split(Pattern.quote(SEP));
        return new ActivityType(Type.valueOf(li[0]), Integer.parseInt(li[1]));
    }
    public String toString() {
        return type.name()+SEP+i;
    }
}
