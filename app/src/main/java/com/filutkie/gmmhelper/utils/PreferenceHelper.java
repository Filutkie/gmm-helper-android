package com.filutkie.gmmhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String KEY_PREF_MARKER_NUMBER = "marker_number";

    public static int nextMarkerNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_PREF_MARKER_NUMBER, prefs.getInt(KEY_PREF_MARKER_NUMBER, 0) + 1).apply();
        return prefs.getInt(KEY_PREF_MARKER_NUMBER, 0);
    }

}
