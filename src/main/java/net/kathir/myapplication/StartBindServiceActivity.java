package net.kathir.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static net.kathir.myapplication.LocationBindService.MSG_SAY_HELLO;

public class StartBindServiceActivity extends AppCompatActivity{


    private static final String TAG = StartBindServiceActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Button bindService, unbindService;

    // A reference to the service used to get location updates.
    private LocationBindService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    static final int MSG_SAY_HELLO2 = 1;

    Messenger mServiceMessenger;

    Messenger mMainActivityMessenger = new Messenger(new ActivityHandler());

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {


           Messenger binder = new Messenger(service);

           // mServiceMessenger = new Messenger(service);
            mBound = true;

            Message msg = Message.obtain(null,MSG_SAY_HELLO, 0, 0);
            try {
                binder.send(msg);
                Log.d(TAG,"SERVICE_MESSANGER "+msg.arg1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

//            if (mServiceMessenger != null) {
//                Message msg = Message.obtain();
//                msg.replyTo = mMainActivityMessenger;
//                try {
//                    mServiceMessenger.send(msg);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }


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

        bindService = (Button)findViewById(R.id.bind_location_updates_button);
        unbindService = (Button)findViewById(R.id.unbind_location_updates_button);


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

        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                   // mService.requestLocationUpdates();
                }
            }
        });


        bindService(new Intent(this, LocationBindService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
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
                            ActivityCompat.requestPermissions(StartBindServiceActivity.this,
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
            ActivityCompat.requestPermissions(StartBindServiceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public class ActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case MSG_SAY_HELLO:
                    Bundle data = msg.getData();
                    Log.d(TAG,"ONACTIVITYHANLDERMESSAGE " + msg.arg1);
                   // String text = data.getString(msg.arg1);
                    break;
            }



        }
    }
}
