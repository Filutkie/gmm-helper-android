package com.filutkie.gmmhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class PreferenceHelper {

    private static final String KEY_PREF_MARKER_NUMBER = "marker_number";
    private static final String KEY_PREF_LAST_LATITUDE = "last_latitude";
    private static final String KEY_PREF_LAST_LONGITUDE = "last_longitude";
    private static final String KEY_PREF_LAST_ZOOM = "last_zoom";

    public static int nextMarkerNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_PREF_MARKER_NUMBER, prefs.getInt(KEY_PREF_MARKER_NUMBER, 0) + 1).apply();
        return prefs.getInt(KEY_PREF_MARKER_NUMBER, 0);
    }

    public static LatLng getLastCameraLatLng(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return new LatLng(prefs.getFloat(KEY_PREF_LAST_LATITUDE, 0.0f), prefs.getFloat(KEY_PREF_LAST_LONGITUDE, 0.0f));
    }

    public static float getLastCameraZoom(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_PREF_LAST_ZOOM, 0);
    }

    public static void saveLastCameraLatLngZoom(Context context, double latitude, double longitude, double zoom) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_PREF_LAST_LATITUDE, (float) latitude).apply();
        prefs.edit().putFloat(KEY_PREF_LAST_LONGITUDE, (float) longitude).apply();
        prefs.edit().putFloat(KEY_PREF_LAST_ZOOM, (float) zoom).apply();
    }
}
