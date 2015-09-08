package com.filutkie.gmmhelper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class FeatureProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper mOpenHelper;

    public static final int MARKER = 10;
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
                FeatureProvider.MARKER);
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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MARKER:
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
                String whereClauseId = FeatureContract.MarkerEntry.COLUMN_TITLE + " = ?";
                String[] whereId = { uri.getLastPathSegment() };
                Log.d("Provider", "with title called: " + uri.toString());
                cursor = mOpenHelper.getReadableDatabase().query(
                        FeatureContract.MarkerEntry.TABLE_NAME,
                        projection,
                        whereClauseId,
                        whereId,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKER_WITH_TITLE:
                String whereClause = FeatureContract.MarkerEntry.COLUMN_TITLE + " LIKE ?";
                String[] where = { uri.getLastPathSegment() + "%" };
                Log.d("Provider", "with title called: " + uri.toString());
                cursor = mOpenHelper.getReadableDatabase().query(
                        FeatureContract.MarkerEntry.TABLE_NAME,
                        projection,
                        whereClause,
                        where,
                        null,
                        null,
                        sortOrder
                );
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
            case MARKER:
                long _id = db.insert(FeatureContract.MarkerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(FeatureContract.MarkerEntry.CONTENT_URI, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MARKER:
                return FeatureContract.MarkerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
