package com.aurora.oasisplanner.data.tags;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

public enum Importance {
    unimportant, regular, important, iimportant;

    /** [DRAWABLE ICON, STRING BELOW, COLOR PRIMARY, COLOR SECONDARY] */
    public static int[][] importanceLevelIds = {
            {R.drawable.ic_trashcan,  R.string.unimportant,   R.color.pr_unimportant,   R.color.sc_unimportant, R.drawable.ic_trashcan},
            {R.drawable.ic_circle,    R.string.standard,      R.color.pr_standard,      R.color.sc_standard, R.drawable.ic_circle},
            {R.drawable.ic_important, R.string.semiimportant, R.color.pr_semi_important,R.color.sc_semi_important, R.drawable.ic_important_simple},
            {R.drawable.ic_iimportant,R.string.important,     R.color.pr_important,     R.color.sc_important, R.drawable.ic_iimportant_simple}};

    public int getImportance() {
        return this.ordinal();
    }
    public Drawable getDrawable() {
        return Resources.getDrawable(importanceLevelIds[getImportance()][Resources.DRAWABLE]);
    }
    public Drawable getSimpleDrawable() {
        return Resources.getDrawable(importanceLevelIds[getImportance()][Resources.SIMPLEDRAWABLE]);
    }
    public int getColorPr(){
        return Resources.getColor(importanceLevelIds[getImportance()][Resources.COLOR_PR]);
    }
    public int getColorSc(){
        return Resources.getColor(importanceLevelIds[getImportance()][Resources.COLOR_SC]);
    }
    public int getTagPr(){
        return importanceLevelIds[getImportance()][Resources.STRING];
    }
    @DrawableRes
    public int getNotifIcon() {
        if (this.equals(important))
            return R.drawable.g_important;
        if (this.equals(iimportant))
            return R.drawable.g_iimportant;
        return R.drawable.g_regular_v2;
    }
    public static Importance max(Importance a, Importance b) {
        return (a.ordinal() > b.ordinal()) ? a : b;
    }
    public String toString() {
        return Resources.getString(importanceLevelIds[getImportance()][Resources.STRING]);
    }
}
