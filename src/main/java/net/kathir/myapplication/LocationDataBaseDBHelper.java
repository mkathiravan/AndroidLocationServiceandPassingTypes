package net.kathir.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDataBaseDBHelper extends SQLiteOpenHelper {


    public LocationDataBaseDBHelper(Context context) {
        super(context, LocationDatabase.DATABASE_NAME, null, LocationDatabase.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        LocationEntryTable.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        LocationEntryTable.onUpgrade(db,oldVersion,newVersion);

    }
}
