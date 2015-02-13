package com.filutkie.gmmhelper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    public static final String DATABASE_NAME = "gmmhelper.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_MARKERS = "markers";

    public static final String COLUMN_MARKER_TITLE = "title";
    public static final String COLUMN_MARKER_DESCRIPTION = "description";
    public static final String COLUMN_MARKER_ADDRESS = "address";
    public static final String COLUMN_MARKER_LATITUDE = "latitude";
    public static final String COLUMN_MARKER_LONGITUDE = "longitude";

    public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_MARKERS + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_MARKER_TITLE + " VARCHAR,"
                + COLUMN_MARKER_DESCRIPTION + " VARCHAR,"
                + COLUMN_MARKER_ADDRESS + " VARCHAR,"
                + COLUMN_MARKER_LATITUDE + " VARCHAR,"
                + COLUMN_MARKER_LONGITUDE + " VARCHAR)";
        Log.d("DatabaseHelper", "SQL onCreate: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(db);
    }
}
