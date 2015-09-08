package com.filutkie.gmmhelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.filutkie.gmmhelper.data.FeatureContract.MarkerEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    public static final String DATABASE_NAME = "gmmhelper.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_MARKERS = "markers";

    public static final String COLUMN_MARKER_ID = "_id";
    public static final String COLUMN_MARKER_TITLE = "title";
    public static final String COLUMN_MARKER_DESCRIPTION = "description";
    public static final String COLUMN_MARKER_ADDRESS = "address";
    public static final String COLUMN_MARKER_LATITUDE = "latitude";
    public static final String COLUMN_MARKER_LONGITUDE = "longitude";

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + MarkerEntry.TABLE_NAME
                + " ("
                + MarkerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + MarkerEntry.COLUMN_TITLE + " VARCHAR,"
                + MarkerEntry.COLUMN_DESCRIPTION + " VARCHAR,"
                + MarkerEntry.COLUMN_ADDRESS + " VARCHAR,"
                + MarkerEntry.COLUMN_LATITUDE + " VARCHAR,"
                + MarkerEntry.COLUMN_LONGITUDE + " VARCHAR"
                + ")";
        Log.d("DatabaseHelper", "SQL onCreate: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade called");
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(db);
    }
}
