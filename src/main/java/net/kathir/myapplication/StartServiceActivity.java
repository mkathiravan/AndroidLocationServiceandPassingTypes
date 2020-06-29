package net.kathir.myapplication;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartServiceActivity extends AppCompatActivity {

    private static final String TAG = StartServiceActivity.class.getSimpleName();
    Button requestLocation, removeLocation;
    private MyReceiver myReceiver;
    LocationTable locationTable = new LocationTable();
    public static AppDatabase db;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LocationAdapter mAdapter;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_service_main);

        checkLocationPermission();

        requestLocation = (Button)findViewById(R.id.request_location_updates_button);
        removeLocation = (Button)findViewById(R.id.remove_location_updates_button);

        ButterKnife.bind(this);

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"location_db").allowMainThreadQueries().build();

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        myReceiver = new MyReceiver();

        requestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    Intent intent = new Intent(StartServiceActivity.this, LocationRequestService.class);
                    startForegroundService(intent);
                }


            }
        });

        removeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(StartServiceActivity.this, "Location Service is Stoppped", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    Intent intent = new Intent(StartServiceActivity.this, LocationRequestService.class);
                    stopService(intent);
                }

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationRequestService.ACTION_BROADCAST));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private class MyReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationRequestService.EXTRA_LOCATION);

            if (location != null) {
                Log.d(TAG,"BROACASTCALLED "+location.getLongitude() + "LOND "+location.getLatitude());

                insert_location(location);
            }
        }
    }

    private void insert_location(Location location) {

        locationTable.setLatitude(location.getLatitude());
        locationTable.setLongitude(location.getLongitude());
        locationTable.setTime(location.getTime());
        StartServiceActivity.db.locationDao().insert(locationTable);

        List<LocationTable> locationList = StartServiceActivity.db.locationDao().getAll();
        Log.wtf(TAG,"LOCATIOSN "+locationList.size());
        Log.wtf(TAG,"GETLATITUDE  "+locationList.get(0).getLatitude());


        mAdapter = new LocationAdapter(StartServiceActivity.this, locationList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void checkLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }
}
