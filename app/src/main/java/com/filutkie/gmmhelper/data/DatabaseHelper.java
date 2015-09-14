package com.filutkie.gmmhelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.filutkie.gmmhelper.data.FeatureContract.MarkerEntry;
import com.filutkie.gmmhelper.data.FeatureContract.PhotoEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    public static final String DATABASE_NAME = "gmmhelper.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_MARKERS = "markers";

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
        final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + MarkerEntry.TABLE_NAME
                + " ("
                + MarkerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + MarkerEntry.COLUMN_TITLE + " VARCHAR, "
                + MarkerEntry.COLUMN_DESCRIPTION + " VARCHAR, "
                + MarkerEntry.COLUMN_ADDRESS + " VARCHAR, "
                + MarkerEntry.COLUMN_LATITUDE + " VARCHAR, "
                + MarkerEntry.COLUMN_LONGITUDE + " VARCHAR, "
                + MarkerEntry.COLUMN_TIME_ADDED + " INTEGER "
                + ");"
                + "CREATE TABLE IF NOT EXISTS " + FeatureContract.PhotoEntry.TABLE_NAME
                + " ("
                + PhotoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + PhotoEntry.COLUMN_MARKER_ID + " INTEGER NOT NULL, "
                + PhotoEntry.COLUMN_URI_PATH + " VARCHAR, "
                + PhotoEntry.COLUMN_TIME_ADDED + " INTEGER, "
                + PhotoEntry.COLUMN_LATITUDE + " VARCHAR, "
                + PhotoEntry.COLUMN_LONGITUDE + " VARCHAR "
                + ");";
        Log.d("DatabaseHelper", "SQL onCreate: " + SQL_CREATE);
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade called");
        onCreate(db);
    }
}
