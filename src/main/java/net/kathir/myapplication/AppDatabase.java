package net.kathir.myapplication;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {LocationTable.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

//    public static AppDatabase appDatabase = null;
//
//    public static AppDatabase getInstance(Context context)
//    {
//        if (appDatabase == null) {
//            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
//                    AppDatabase.class, "location_db").build();
//        }
//        return appDatabase;
//    }

    public abstract LocationDao locationDao();

}
