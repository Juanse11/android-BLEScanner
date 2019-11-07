package com.example.bledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bledemo.adapters.BluetoothDeviceListAdapter;
import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.BLEManagerCallerInterface;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BLEManagerCallerInterface {

    public BLEManager bleManager;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_stop_scan_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bleManager!=null){
                    bleManager.scanDevices();
                }
            }
        });

        bleManager=new BLEManager(this,this);
        if(!bleManager.isBluetoothOn()){
            bleManager.enableBluetoothDevice(this, 1001);
        }else{
            bleManager.requestLocationPermissions(this,1002);
        }
        mainActivity=this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspec

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allPermissionsGranted=true;
        if (requestCode == 1002) {
            for (int currentResult:grantResults
            ) {
                if(currentResult!= PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted=false;
                    break;
                }
            }
            if(!allPermissionsGranted){
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setTitle("Permissions")
                        .setMessage("Camera and Location permissions must be granted in order to execute the app")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{

            if(requestCode==1001){
                if(resultCode!=Activity.RESULT_OK){

                }else{
                    bleManager.requestLocationPermissions(this,1002);

                }
            }


        }catch (Exception error){

        }
    }

    @Override
    public void scanStartedSuccessfully() {

    }

    @Override
    public void scanStoped() {

    }

    @Override
    public void scanFailed(int error) {

    }

    @Override
    public void newDeviceDetected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    ListView listView=(ListView)findViewById(R.id.devices_list_id);
                    BluetoothDeviceListAdapter adapter=new BluetoothDeviceListAdapter(getApplicationContext(),bleManager.scanResults,mainActivity);
                    listView.setAdapter(adapter);

                }catch (Exception error){

                }

            }
        });


    }

    @Override
    public void servicesDiscovered(final List<BluetoothGattService> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (BluetoothGattService s: services) {
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = s.getCharacteristics();
                    for (BluetoothGattCharacteristic c: bluetoothGattCharacteristics) {

                    }
                }
                Toast.makeText(mainActivity, services.toArray().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
