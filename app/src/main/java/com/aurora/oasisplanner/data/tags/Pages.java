package com.aurora.oasisplanner.data.tags;

import androidx.annotation.IdRes;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

public enum Pages {
    HOME, DASHBOARD, EVENTARRANGER, PROJECTS, SETTINGS, MEMOS, ANALYTICS;

    @IdRes
    public static int[] navList = new int[]{
            R.id.navigation_home,
            R.id.navigation_eventarranger,
            R.id.navigation_eventarranger,
            R.id.navigation_project,
            R.id.navigation_settings,
            R.id.navigation_memos,
            R.id.navigation_project
    };
    @IdRes
    public static int[] sideNavList = new int[]{
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_eventarranger,
            R.id.navigation_project,
            R.id.navigation_settings,
            R.id.navigation_memos,
            R.id.navigation_analytics
    };

    @IdRes
    public int getNav() {
        return navList[ordinal()];
    }
    @IdRes
    public int getSideNav() {
        return sideNavList[ordinal()];
    }
    public String getTitle() { return Resources.getString(Resources.frameTitles[ordinal()]); }
}
