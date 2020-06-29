package net.kathir.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationEntryTable {


    private static final String TAG = LocationEntryTable.class.getSimpleName();

    public static final String COLUMN_ID = "_id";

    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String LOCATION_TIMESTAMP = "location_timestamp";
    public static final String LOCATION_TABLE = "location_table";



    //Database creation SQL statement

    private static final String LOCATION_ENTRY_TABLE = "create table "
            + LOCATION_TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + LOCATION_LATITUDE + " text null,"
            + LOCATION_LONGITUDE + " int not null,"
            + LOCATION_TIMESTAMP + " text null,"
            + " text null"
            + ");";


    public static void onCreate(SQLiteDatabase database)
    {
        Log.d(TAG,"Creating Database ");
        database.execSQL(LOCATION_ENTRY_TABLE);
    }

    public static void onDelete(SQLiteDatabase database)
    {
        Log.d(TAG,"Delete Database");
        database.delete(LOCATION_ENTRY_TABLE,null,null);
    }


    public static void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion)
    {
        Log.d(TAG,"Upgrading database from version "
                + oldVersion + "to "+ newVersion
                + ", which will destroy all old data");

        database.execSQL("DROP TABLE IF EXISTS" + LOCATION_ENTRY_TABLE);
        onCreate(database);
    }



}
