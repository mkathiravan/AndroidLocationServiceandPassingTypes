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
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;

public class AIDLServiceActivity extends AppCompatActivity {

    private static final String TAG = AIDLServiceActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Button bindService, unbindService;
    IRemoteLocationService mService;
    private Location location;
    Realm realm;
    List<LocationDetailModel> locationDetailModelList;

    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Cursor locationyValues;
    Intent intent;
    IRemoteLocationService iRemoteLocationService;
    AddServiceConnection connection;




    class AddServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mService = IRemoteLocationService.Stub.asInterface((IBinder)boundService);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        bindService = (Button)findViewById(R.id.bind_location_updates_button);
        unbindService = (Button)findViewById(R.id.unbind_location_updates_button);


        connection =  new AddServiceConnection();

        locationDetailModelList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mService.requestLocationUpdates();

            }
        });

        unbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // mService.removeLocationUpdates();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"OnStart is called ");

        bindService(new Intent(this, AIDLSerivce.class), connection,
                Context.BIND_AUTO_CREATE);


        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
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
                            ActivityCompat.requestPermissions(AIDLServiceActivity.this,
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
            ActivityCompat.requestPermissions(AIDLServiceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }





    @Override
    public void onStop() {
        super.onStop();
    }




}
