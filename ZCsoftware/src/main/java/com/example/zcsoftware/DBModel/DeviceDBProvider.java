package com.example.zcsoftware.DBModel;

import android.content.ContentProvider;
import android.content.ContentResolver;
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

    public static final int SETPOST_VALUE = 1;
    public static final int UNSETPOST_VALUE = 0;




    // Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_MAC = "MacAddress";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ALIAS = "Alias";
    public static final String KEY_IMAGENAME = "ImageName";
    public static final String KEY_DETAILS = "Details";
    public static final String KEY_LGCTYPE = "LgcType";
    public static final String KEY_LOCLAN = "Latitude";
    public static final String KEY_LOCLONG = "Longitude";
    public static final String KEY_ITFTYPE = "ItfType";
    public static final String KEY_POST = "Post";
    public static final String KEY_LOST = "Lost";
    public static final String KEY_REVINT1 = "RevInt1";
    public static final String KEY_REVINT2 = "RevInt2";
    public static final String KEY_REVINT3 = "RevInt3";
    public static final String KEY_REVINT4 = "RevInt4";
    public static final String KEY_REVINT5 = "RevInt5";
    public static final String KEY_REVSTR1 = "RevStr1";
    public static final String KEY_REVSTR2 = "RevStr2";
    public static final String KEY_REVSTR3 = "RevStr3";
    public static final String KEY_REVSTR4 = "RevStr4";
    public static final String KEY_REVSTR5 = "RevStr5";


    // Column indexes
    public static final int ID_COLUMN = 0;
    public static final int MAC_COLUMN = 1;
    public static final int NAME_COLUMN = 2;
    public static final int ALIAS_COLUMN = 3;
    public static final int IMAGENAME_COLUMN = 4;
    public static final int DETAILS_COLUMN = 5;
    public static final int LGCTYPE_COLUMN = 6;
    public static final int LOCLAN_COLUMN = 7;
    public static final int LOCLONG_COLUMN = 8;
    public static final int ITFTYPE_COLUMN = 9;
    public static final int POST_COLUMN = 10;
    public static final int LOST_COLUMN = 11;

    public static final int REVINT1_COLUMN = 12;
    public static final int REVINT2_COLUMN = 13;
    public static final int REVINT3_COLUMN = 14;
    public static final int REVINT4_COLUMN = 15;
    public static final int REVINT5_COLUMN = 16;

    public static final int REVSTR1_COLUMN = 17;
    public static final int REVSTR2_COLUMN = 18;
    public static final int REVSTR3_COLUMN = 19;
    public static final int REVSTR4_COLUMN = 20;
    public static final int REVSTR5_COLUMN = 21;




    public static final String DATABASE_CREATE =
            "create table " + DEVICE_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_MAC + " TEXT, "
                    + KEY_NAME + " TEXT, "
                    + KEY_ALIAS + " TEXT, "
                    + KEY_IMAGENAME + " TEXT, "
                    + KEY_DETAILS + " TEXT, "
                    + KEY_LGCTYPE + " integer, "
                    + KEY_LOCLAN + " double, "
                    + KEY_LOCLONG + " double, "
                    + KEY_ITFTYPE + " integer, "
                    + KEY_POST + " integer, "
                    + KEY_LOST + " integer, "
                    + KEY_REVINT1 + " integer, "
                    + KEY_REVINT2 + " integer, "
                    + KEY_REVINT3 + " integer, "
                    + KEY_REVINT4 + " integer, "
                    + KEY_REVINT5 + " integer, "
                    + KEY_REVSTR1 + " TEXT, "
                    + KEY_REVSTR2 + " TEXT, "
                    + KEY_REVSTR3 + " TEXT, "
                    + KEY_REVSTR4 + " TEXT, "
                    + KEY_REVSTR5 + " TEXT);";




    private static final int DEVICES = 1;
    private static final int DEVICES_ID = 2;

    private static final String URI_STR = "content://com.example.zcsoftware.deviceprovider";
    public final static Uri CONTENT_URI = Uri.parse(URI_STR);
    public final static Uri CONTENT_URI_DEVICE_ALL = Uri.parse("content://com.example.zcsoftware.deviceprovider/devices");

    //public final static Uri CONTENT_URI_DEVICE_NO = Uri.parse("content://com.example.zcsoftware.DBModel/devices/#");


    private static UriMatcher uriMatcher ;




    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.zcsoftware.deviceprovider","devices",DEVICES);
        uriMatcher.addURI("com.example.zcsoftware.deviceprovider","devices/#",DEVICES_ID);
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
    public  Uri insert(Uri _uri, ContentValues values) {
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
    public  int delete(Uri uri, String selection, String[] selectionArgs) {
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
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        int n = uriMatcher.match(uri);
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


    public static void setDeviceCloudPost(ContentResolver cr, int itfType, String macInfo, int iPost)
    {
        String where = DeviceDBProvider.KEY_ITFTYPE + " = " + String.valueOf(itfType) + " AND "
                 + DeviceDBProvider.KEY_MAC + " = " + "\"" +  macInfo + "\"" ;

        ContentValues values = new ContentValues();
        values.put(DeviceDBProvider.KEY_POST,iPost);

        int count = cr.update(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,values,where,null);

    }

    public static synchronized void updateLocationinfo(ContentResolver cr, double lan, double log, int itfType, String macInfo)
    {
        String where = DeviceDBProvider.KEY_ITFTYPE + " = " + String.valueOf(itfType) + " AND "
                + DeviceDBProvider.KEY_MAC + " = " + "\"" +  macInfo + "\"" ;

        ContentValues values = new ContentValues();
        values.put(DeviceDBProvider.KEY_LOCLAN,lan);
        values.put(DeviceDBProvider.KEY_LOCLONG,log);

        int count = cr.update(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,values,where,null);


    }
}
