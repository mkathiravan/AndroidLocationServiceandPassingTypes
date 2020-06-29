package net.kathir.myapplication;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class ResultReceiverActivity extends AppCompatActivity {

    private static final String TAG = ResultReceiverActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Button bindService, unbindService;
    private ResultReceiverService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    private Location location;
    Realm realm;
    List<LocationDetailModel> locationDetailModelList;
    private RealmLocationAdapter mAdapter;

    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Cursor locationyValues;
    MyResultReceiver resultReceiver;
    Intent intent;
    Realm mRealm;

    LocationRealmModel obj;



    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ResultReceiverService.LocalResultBinder binder = (ResultReceiverService.LocalResultBinder) service;
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

        mRealm = Realm.getDefaultInstance();


        locationDetailModelList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        resultReceiver = new MyResultReceiver(null);
        intent = new Intent(this, ResultReceiverService.class);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);


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

        ResultReceiver receiver=new MyResultReceiver(null);

        bindService(new Intent(this, ResultReceiverService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);


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
                            ActivityCompat.requestPermissions(ResultReceiverActivity.this,
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
            ActivityCompat.requestPermissions(ResultReceiverActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    class MyResultReceiver extends ResultReceiver {

        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode==RESULT_OK && resultData!=null){

                final Double latitude = resultData.getDouble("latitude");
                final Double longitude = resultData.getDouble("longitude");
                final long timestamp = resultData.getLong("timestamp");

                Log.d(TAG,"RECEIVELATITUDE "+ latitude);


                mRealm.beginTransaction();
                LocationRealmModel locationModel = mRealm.createObject(LocationRealmModel.class);
                locationModel.setLatitude(latitude);
                locationModel.setLongitude(longitude);
                locationModel.setTimeStamp(timestamp);
                mRealm.commitTransaction();



                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            obj = new LocationRealmModel();
                            obj.setLatitude(latitude);
                            obj.setLongitude(longitude);
                            obj.setTimeStamp(timestamp);

                            retrieveDataFromDb();
                        }
                    });
                } finally {
                    if(realm != null) {
                        realm.close();
                    }
                }




            }
        }
    }

    private void retrieveDataFromDb() {

        Realm mRealm;
        mRealm = Realm.getDefaultInstance();
        RealmResults<LocationRealmModel> realmCities = mRealm.where(LocationRealmModel.class).findAllAsync();

        //RealmResults<LocationRealmModel> realmCities= realm.where(LocationRealmModel.class).findAllAsync();
        realmCities.load();


        mAdapter = new RealmLocationAdapter(ResultReceiverActivity.this, realmCities);
        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onStop() {
        super.onStop();
    }









}
