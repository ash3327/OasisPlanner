package com.aurora.oasisplanner.util.styling;

import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;

public class Resources {
    /** indices of the required resources in the Ids list. */
    public static int DRAWABLE = 0, STRING = 1, COLOR_PR = 2, COLOR_SC = 3, SIMPLEDRAWABLE = 4;

    public static int[] frameTitles = {
            R.string.title_home, R.string.title_dashboard,
            R.string.title_eventarranger, R.string.title_projects,
            R.string.title_settings
    };

    public static Drawable getDrawable(int drawableId) {
        return ResourcesCompat.getDrawable(
                MainActivity.main.getResources(),
                drawableId,
                MainActivity.main.getTheme());
    }
    public static float getDimension(int dimenId){
        return MainActivity.main.getResources().getDimension(dimenId);
    }
    public static String getString(int stringId){
        return MainActivity.main.getResources().getString(stringId);
    }

    public static int getColor(int id){
        return MainActivity.main.getColor(id);
    }
    public static int getInt(int id) {return MainActivity.main.getResources().getInteger(id);}
    public static String[] getStringArr(int stringArrId) {
        return MainActivity.main.getResources().getStringArray(stringArrId);
    }
}
