package net.kathir.myapplication;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startService,bindService, bindServiceEventBus,bindServiceResultReciever,aidlService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService = (Button)findViewById(R.id.start_service);
        bindService = (Button)findViewById(R.id.bind_service);
        bindServiceEventBus = (Button) findViewById(R.id.bind_service_connection);
        bindServiceResultReciever = (Button)findViewById(R.id.bind_service_resultreceiver);
        aidlService = (Button)findViewById(R.id.bind_aidlservice);





        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StartServiceActivity.class);
                startActivity(intent);
            }
        });

        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StartBindServiceActivity.class);
                startActivity(intent);
            }
        });


        bindServiceEventBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,BindServiceCommunActivity.class);
                startActivity(intent);

            }
        });

        bindServiceResultReciever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,ResultReceiverActivity.class);
                startActivity(intent);

            }
        });


        aidlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,AIDLServiceActivity.class);
                startActivity(intent);


            }
        });
    }






}
