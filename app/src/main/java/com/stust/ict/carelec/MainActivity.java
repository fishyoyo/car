package com.stust.ict.carelec;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.SpeedView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {
    BluetoothSPP bt;
    AwesomeSpeedometer Speed;
    TextView speedLabel ,speedLabe2 ,speedLabe3, speedLabe4, speedLabe5,speedLabe6,speedLabe7;
    Button Send,BT1;
    Button btnConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
        speedLabel = (TextView)findViewById(R.id.speedLabel);
        speedLabe2 = (TextView)findViewById(R.id.speedLabe2);
        speedLabe3 = (TextView)findViewById(R.id.speedLabe3);
        speedLabe4 = (TextView)findViewById(R.id.speedLabe4);
        speedLabe5 = (TextView)findViewById(R.id.speedLabe5);
        speedLabe6 = (TextView)findViewById(R.id.speedLabe6);
        speedLabe7 = (TextView)findViewById(R.id.speedLabe7);
        Speed=(AwesomeSpeedometer) findViewById(R.id.Speed);
        Speed.setMaxSpeed(250);
        Speed.setTrianglesColor(Color.YELLOW);
        Send = (Button)findViewById(R.id.Send);
        BT1 = (Button)findViewById(R.id.BT1);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup();
            }
        });
        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up();
            }
        });
        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    private void up() {
        bt.send("@data",true);
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener(){
            public  void  onDataReceived(byte [] data,String  message){
                Log.v("tag",message);
                String Result[] = message.split(",");
                if(Result[0].toString().equals("$210")){
                    Speed.speedTo(Integer.parseInt(Result[1].toString()));
                    speedLabel.setText(Result[1].toString());
                    speedLabe2.setText(Result[2].toString());
                    speedLabe3.setText(Result[3].toString());
                    speedLabe4.setText(Result[4].toString());
                    speedLabe5.setText(Result[5].toString());
                    speedLabe6.setText(Result[6].toString());
                    speedLabe7.setText(Result[7].toString());
                }
            }
        });

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void setup() {
        bt.send("$210,144,50,135,50,105,65535,4998,",true);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
