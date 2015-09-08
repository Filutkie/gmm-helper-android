package com.filutkie.gmmhelper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.filutkie.gmmhelper.model.MyMarker;

import java.util.ArrayList;
import java.util.List;

public class MyMarkerDatasource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public MyMarkerDatasource(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public List<MyMarker> getAllMarkers() {
        database = dbHelper.getWritableDatabase();
        List<MyMarker> markerList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MARKERS,
                null, null, null, null, null, null);
        int idColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_ID);
        int titleColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_TITLE);
        int descriptionColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_DESCRIPTION);
        int addressColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_ADDRESS);
        int latitudeColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_LATITUDE);
        int longitudeColIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MARKER_LONGITUDE);

        MyMarker marker;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                marker = new MyMarker()
                        .id(cursor.getInt(idColIndex))
                        .title(cursor.getString(titleColIndex))
                        .description(cursor.getString(descriptionColIndex))
                        .address(cursor.getString(addressColIndex))
                        .latitude(cursor.getDouble(latitudeColIndex))
                        .longitude(cursor.getDouble(longitudeColIndex));
                markerList.add(marker);
                cursor.moveToNext();
            }
        }
        cursor.close();
        dbHelper.close();
        return markerList;
    }

    public Cursor getAllMarkersCursor() {
        return database.query(DatabaseHelper.TABLE_MARKERS,
                null, null, null, null, null, null);
    }

    public int addMarker(MyMarker marker) {
        Log.d("DatabaseHelper", "Adding marker: " + marker.toString());
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int markerId = 0;
        try {
            database.beginTransaction();
            values.put(DatabaseHelper.COLUMN_MARKER_TITLE, marker.getTitle());
            values.put(DatabaseHelper.COLUMN_MARKER_DESCRIPTION, marker.getDescription());
            values.put(DatabaseHelper.COLUMN_MARKER_ADDRESS, marker.getAddress());
            values.put(DatabaseHelper.COLUMN_MARKER_LATITUDE, marker.getLatitude());
            values.put(DatabaseHelper.COLUMN_MARKER_LONGITUDE, marker.getLongitude());
            markerId = (int) database.insert(DatabaseHelper.TABLE_MARKERS, null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            dbHelper.close();
        }
        return markerId;
    }
}
