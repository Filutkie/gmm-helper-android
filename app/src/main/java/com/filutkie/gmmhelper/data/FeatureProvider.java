package com.filutkie.gmmhelper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.filutkie.gmmhelper.data.FeatureContract.MarkerEntry;

public class FeatureProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper mOpenHelper;

    public static final int MARKERS = 10;
    public static final int MARKER_WITH_ID = 11;
    public static final int MARKER_WITH_TITLE = 12;

    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(
                FeatureContract.CONTENT_AUTHORITY,
                FeatureContract.PATH_MARKER,
                FeatureProvider.MARKERS);
        uriMatcher.addURI(
                FeatureContract.CONTENT_AUTHORITY,
                FeatureContract.PATH_MARKER + "/#",
                FeatureProvider.MARKER_WITH_ID);
        uriMatcher.addURI(
                FeatureContract.CONTENT_AUTHORITY,
                FeatureContract.PATH_MARKER + "/*",
                FeatureProvider.MARKER_WITH_TITLE);

        return uriMatcher;
    }

    private Cursor getMarkerById(Uri uri, String[] projection) {
        int id = MarkerEntry.getIdFromUri(uri);
        String selection = MarkerEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        return mOpenHelper.getReadableDatabase().query(
                FeatureContract.MarkerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getMarkerByTitle(Uri uri, String[] projection, String sortOrder) {
        String selection = FeatureContract.MarkerEntry.COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = {uri.getLastPathSegment() + "%"};
        return mOpenHelper.getReadableDatabase().query(
                FeatureContract.MarkerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MARKERS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FeatureContract.MarkerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKER_WITH_ID:
                Log.d("Provider", "marker with id: " + uri.toString());
                cursor = getMarkerById(uri, projection);
                break;
            case MARKER_WITH_TITLE:
                Log.d("Provider", "marker with title: " + uri.toString());
                cursor = getMarkerByTitle(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case MARKERS:
                long _id = db.insert(MarkerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(MarkerEntry.CONTENT_URI, _id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MARKERS:
                count = db.update(MarkerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MARKER_WITH_ID:
                String segment = uri.getPathSegments().get(1);
                count = db.update(
                        MarkerEntry.TABLE_NAME,
                        values, MarkerEntry._ID + "=" + segment
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MARKERS:
                count = db.delete(MarkerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MARKER_WITH_ID:
                String id = uri.getLastPathSegment();
                count = db.delete(MarkerEntry.TABLE_NAME,
                        MarkerEntry._ID + "=" + id
                                + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MARKERS:
                return MarkerEntry.CONTENT_TYPE_DIR;
            case MARKER_WITH_ID:
            case MARKER_WITH_TITLE:
                return MarkerEntry.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
