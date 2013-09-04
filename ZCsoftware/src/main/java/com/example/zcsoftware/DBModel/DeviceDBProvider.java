package com.example.zcsoftware.DBModel;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by user on 7/28/13.
 */
public class DeviceDBProvider extends ContentProvider {

    //The underlying database
    private SQLiteDatabase deviceDB;

    private static final String TAG = "ZCSoftwareProvider";
    private static final String DATABASE_NAME = "ZCSoftwareProvider.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DEVICE_TABLE = "devices";
    // Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_DETAILS = "Details";
    public static final String KEY_ALIAS = "Alias";
    public static final String KEY_MAC = "MacAddress";
    public static final String KEY_TYPE = "ItfType";
    public static final String KEY_IDENTIFY = "Identify";
    // Column indexes
    public static final int ID_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int DETAILS_COLUMN = 2;
    public static final int ALIAS_COLUMN = 3;
    public static final int MAC_COLUMN = 4;
    public static final int TYPE_COLUMN = 5;
    public static final int IDENTIFY_COLUMN = 6;



    public static final String DATABASE_CREATE =
            "create table " + DEVICE_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " TEXT, "
                    + KEY_DETAILS + " TEXT, "
                    + KEY_ALIAS + " TEXT, "
                    + KEY_MAC + " TEXT, "
                    + KEY_TYPE + " integer, "
                    + KEY_IDENTIFY + " TEXT);";


    private static final int DEVICES = 1;
    private static final int DEVICES_ID = 2;

    public final static Uri CONTENT_URI = Uri.parse("content://com.example.zcsoftware.DBModel");
    public final static Uri CONTENT_URI_DEVICE_ALL = Uri.parse("content://com.example.zcsoftware.DBModel/devices");
    public final static Uri CONTENT_URI_DEVICE_NO = Uri.parse("content://com.example.zcsoftware.DBModel/devices/#");


    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);




    static
    {
        uriMatcher.addURI("content://com.example.zcsoftware.DBModel","devices",DEVICES);
        uriMatcher.addURI("content://com.example.zcsoftware.DBModel","devices/#",DEVICES_ID);
    }



    @Override
    public boolean onCreate() {

        Context context = getContext();
        DeviceDBHelper dbHelper = new DeviceDBHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
        deviceDB = dbHelper.getWritableDatabase();
        return deviceDB != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DEVICE_TABLE);

        // If this is a row query, limit the result set to the passed in row.
        int k = uriMatcher.match(uri);
        switch (k) {

            case DEVICES_ID:
                String a = uri.getPathSegments().get(0);
                String b = uri.getPathSegments().get(1);
                String c = KEY_ID + "=" + uri.getPathSegments().get(1);
                qb.appendWhere(c);
                break;
            default      : break;
        }

        // If no sort order is specified sort by date / time
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = KEY_NAME;
        } else {
            orderBy = sortOrder;
        }

        // Apply the query to the underlying database.
        Cursor c = qb.query(deviceDB,
                projection,
                selection, selectionArgs,
                null, null,
                orderBy);

        // Register the contexts ContentResolver to be notified if
        // the cursor result set changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return a cursor to the query result.
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri _uri, ContentValues values) {
        // Insert the new row, will return the row number if
        // successful.
        long rowID = deviceDB.insert(DEVICE_TABLE, "DEVICE", values);


        // Return a URI to the newly inserted row on success.
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI_DEVICE_ALL, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert row into " + _uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        deviceDB.delete(DEVICE_TABLE, selection, selectionArgs);
        switch (uriMatcher.match(uri)) {
            case DEVICES:
                try
                {
                    count = deviceDB.delete(DEVICE_TABLE, selection, selectionArgs);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();;
                    count = 0;
                }
                break;

            case DEVICES_ID:
                String segment = uri.getPathSegments().get(1);
                count = deviceDB.delete(DEVICE_TABLE, KEY_ID + "="
                        + segment
                        + (!TextUtils.isEmpty(selection) ? " AND ("
                        + selection + ')' : ""), selectionArgs);
                break;

            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case DEVICES: count = deviceDB.update(DEVICE_TABLE, values,
                    selection, selectionArgs);
                break;

            case DEVICES_ID: String segment = uri.getPathSegments().get(1);
                count = deviceDB.update(DEVICE_TABLE, values, KEY_ID
                        + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " AND ("
                        + selection + ')' : ""), selectionArgs);
                break;

            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
