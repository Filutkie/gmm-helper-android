package com.filutkie.gmmhelper.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class FeatureContract {

    public static final String CONTENT_AUTHORITY = "com.filutkie.gmmhelper.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MARKER = "marker";
    public static final String PATH_PHOTO = "photo";


    public static final class MarkerEntry implements BaseColumns {

        public static final String TABLE_NAME = "markers";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TIME_ADDED = "time_added";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MARKER).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MARKER;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MARKER;

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getLastPathSegment());
        }
    }

    public static final class PhotoEntry implements BaseColumns {

        public static final String TABLE_NAME = "photos";

        public static final String COLUMN_MARKER_ID = "marker_id";
        public static final String COLUMN_URI_PATH = "uri_path";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TIME_ADDED = "time_added";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHOTO).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHOTO;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHOTO;

    }

}
