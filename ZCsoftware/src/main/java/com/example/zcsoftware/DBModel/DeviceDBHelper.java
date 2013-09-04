package com.example.zcsoftware.DBModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 7/28/13.
 */
public class DeviceDBHelper extends SQLiteOpenHelper {



    public DeviceDBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(DeviceDBProvider.DATABASE_CREATE);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {
            db.execSQL("DROP TABLE IF EXISTS " + DeviceDBProvider.DEVICE_TABLE);

            this.onCreate(db);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();;
        }
    }
}
