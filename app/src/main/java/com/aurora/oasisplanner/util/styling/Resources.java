package com.aurora.oasisplanner.util.styling;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;

public class Resources {
    /** indices of the required resources in the Ids list. */
    public static int DRAWABLE = 0, STRING = 1, COLOR_PR = 2, COLOR_SC = 3, SIMPLEDRAWABLE = 4;

    public static Context context = MainActivity.main;
    public static int[] frameTitles = {
            R.string.title_home, R.string.title_dashboard,
            R.string.title_eventarranger, R.string.title_projects,
            R.string.title_settings
    };

    public static Drawable getDrawable(int drawableId) {
        return ResourcesCompat.getDrawable(
                context.getResources(),
                drawableId,
                context.getTheme());
    }
    public static float getDimension(int dimenId){
        return context.getResources().getDimension(dimenId);
    }
    public static String getString(int stringId){
        return context.getResources().getString(stringId);
    }

    public static int getColor(int id){
        return context.getColor(id);
    }
    public static int getInt(int id) {return context.getResources().getInteger(id);}
    public static String[] getStringArr(int stringArrId) {
        return context.getResources().getStringArray(stringArrId);
    }
}
