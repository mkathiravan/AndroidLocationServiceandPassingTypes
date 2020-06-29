package net.kathir.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BindServiceCommunActivity extends AppCompatActivity {

    private static final String TAG = BindServiceCommunActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Button bindService, unbindService;
    private LocalBindEventComm mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    private Location location;
    List<LocationDetailModel> locationDetailModelList;
    private LocationDetailAdapter mAdapter;

    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Cursor locationyValues;



    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBindEventComm.LocalBinder binder = (LocalBindEventComm.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        bindService = (Button)findViewById(R.id.bind_location_updates_button);
        unbindService = (Button)findViewById(R.id.unbind_location_updates_button);

        locationDetailModelList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationDetailAdapter(this, locationDetailModelList);
        mRecyclerView.setAdapter(mAdapter);


        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mService.requestLocationUpdates();

            }
        });

        unbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mService.removeLocationUpdates();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"OnStart is called ");


        bindService(new Intent(this, LocalBindEventComm.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);

        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    mService.requestLocationUpdates();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(BindServiceCommunActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(BindServiceCommunActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.d(TAG,"BINDER_SERUCEC " +event.getLocation());
        location = event.getLocation();
        insertLocationEnteryInDB(event);
        updateUI();
        Log.d(TAG,"RECEIVE_LATITUDE " + location.getLatitude());

    };


    private long insertLocationEnteryInDB(MessageEvent event) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(LocationEntryTable.LOCATION_LATITUDE,event.getLocation().getLatitude());
        initialValues.put(LocationEntryTable.LOCATION_LONGITUDE,event.getLocation().getLongitude());
        initialValues.put(LocationEntryTable.LOCATION_TIMESTAMP,event.getLocation().getTime());
        Uri uri = this.getContentResolver().insert(LocationEntryContentProvider.CONTENT_URI, initialValues);
        return Long.valueOf(uri.getLastPathSegment());
    }

    private void updateUI() {
        locationyValues = fetchHOSHistoryData();
        constructHOSHistoryArray(locationyValues);

        mAdapter.updateDataSet(locationDetailModelList);
        mAdapter.notifyDataSetChanged();
    }


    private void constructHOSHistoryArray(Cursor mCursor) {

        if (mCursor != null) {
            locationDetailModelList.clear();
            String latitude = "", longitude = "", time_stamp = "", rowId = "";;
            LocationDetailModel locationDetailModel = new LocationDetailModel();
            for (int i = 0; i < mCursor.getCount(); i++) {
                mCursor.moveToPosition(i);
                latitude = mCursor.getString(mCursor.getColumnIndexOrThrow(LocationEntryTable.LOCATION_LATITUDE));
                longitude = mCursor.getString(mCursor.getColumnIndexOrThrow(LocationEntryTable.LOCATION_LONGITUDE));
                time_stamp = mCursor.getString(mCursor.getColumnIndexOrThrow(LocationEntryTable.LOCATION_TIMESTAMP));
                rowId = mCursor.getString(mCursor.getColumnIndexOrThrow(LocationEntryTable.COLUMN_ID));
                LocationDetailModel locationDetailModel1 = new LocationDetailModel(rowId,latitude, longitude, time_stamp);
                locationDetailModelList.add(locationDetailModel1);
            }
        }

    }


    private Cursor fetchHOSHistoryData() {
        String[] projection = {LocationEntryTable.COLUMN_ID, LocationEntryTable.LOCATION_LATITUDE, LocationEntryTable.LOCATION_LONGITUDE, LocationEntryTable.LOCATION_TIMESTAMP};
        Cursor cursor = getContentResolver().query(Uri.parse(LocationEntryContentProvider.CONTENT_URI.toString()),
                projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    Log.d("location_latitude",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(LocationEntryTable.LOCATION_LATITUDE)));
                    Log.d("location_longitude",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(LocationEntryTable.LOCATION_LONGITUDE)));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return cursor;
    }


}
