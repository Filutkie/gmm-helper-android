package com.filutkie.gmmhelper.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        int idColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry._ID);
        int titleColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_TITLE);
        int descriptionColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_DESCRIPTION);
        int addressColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_ADDRESS);
        int latitudeColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_LATITUDE);
        int longitudeColIndex = cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_LONGITUDE);

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
}
