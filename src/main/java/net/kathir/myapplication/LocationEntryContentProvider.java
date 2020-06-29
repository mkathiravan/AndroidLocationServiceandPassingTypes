package net.kathir.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

public class LocationEntryContentProvider extends ContentProvider {

    private static final String TAG = LocationEntryContentProvider.class.getSimpleName();

    //database
    private LocationDataBaseDBHelper database;


    // used for the UriMacher
    private static final int LOCATION_URI_MATCHER = 1;
    private static final int LOCATION_URI_MATCHER_ID = 2;

    private static final String AUTHORITY = "net.kathir.myapplication.locationentrycontentprovider";
    private static final String BASE_BATH_HOSHISTORY_ENTRY = "locationentry";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_BATH_HOSHISTORY_ENTRY);


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        sURIMatcher.addURI(AUTHORITY,BASE_BATH_HOSHISTORY_ENTRY,LOCATION_URI_MATCHER);
        sURIMatcher.addURI(AUTHORITY,BASE_BATH_HOSHISTORY_ENTRY + "/#",LOCATION_URI_MATCHER_ID);
    }

    @Override
    public boolean onCreate() {
        database = new LocationDataBaseDBHelper(getContext());
        return false;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,  @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //Check if the caller has requested a column which does not exists

        checkColumns(projection);

        //Set the Table
        queryBuilder.setTables(LocationEntryTable.LOCATION_TABLE);

        int uriType = sURIMatcher.match(uri);

        switch (uriType)
        {
            case LOCATION_URI_MATCHER:
                break;
            case LOCATION_URI_MATCHER_ID:
                //adding the Id into original query
                queryBuilder.appendWhere(LocationEntryTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);

        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);


        return cursor;
    }


    private void checkColumns(String[] projection) {

        String[] available = {LocationEntryTable.COLUMN_ID, LocationEntryTable.LOCATION_LATITUDE, LocationEntryTable.LOCATION_LONGITUDE, LocationEntryTable.LOCATION_TIMESTAMP};

        if(projection != null)
        {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            if(!availableColumns.containsAll(requestedColumns))
            {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowDeleted = 0;
        long id = 0;

        switch (uriType)
        {
            case LOCATION_URI_MATCHER:
                // zero count meant empty table
                id = sqlDB.insert(LocationEntryTable.LOCATION_TABLE,null,values);
                Log.d(TAG, "insert into employee database success");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(BASE_BATH_HOSHISTORY_ENTRY + "/" +id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,  @Nullable String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType)
        {
            case LOCATION_URI_MATCHER:
                rowsDeleted = sqlDB.delete(LocationEntryTable.LOCATION_TABLE,selection,selectionArgs);
                break;

            case LOCATION_URI_MATCHER_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection))
                {
                    rowsDeleted = sqlDB.delete(LocationEntryTable.LOCATION_TABLE,
                            LocationEntryTable.COLUMN_ID + "=" +id,null);
                }
                else
                {
                    rowsDeleted = sqlDB.delete(LocationEntryTable.LOCATION_TABLE,
                            LocationEntryTable.COLUMN_ID + "=" +id + " and "+selection,selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,  @Nullable String selection,  @Nullable String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType)
        {
            case LOCATION_URI_MATCHER:
                rowsUpdated = sqlDB.update(LocationEntryTable.LOCATION_TABLE,values,selection,selectionArgs);
                break;

            case LOCATION_URI_MATCHER_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection))
                {
                    rowsUpdated = sqlDB.update(LocationEntryTable.LOCATION_TABLE,
                            values,LocationEntryTable.COLUMN_ID + "=" +id,null);

                }
                else
                {
                    rowsUpdated = sqlDB.update(LocationEntryTable.LOCATION_TABLE,
                            values,LocationEntryTable.COLUMN_ID + "=" +id
                                    + " and "
                                    +selection,selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }
}
