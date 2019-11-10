package com.example.bledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bledemo.adapters.BluetoothDeviceListAdapter;
import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.BLEManagerCallerInterface;
import com.example.bledemo.fragments.DeviceDetailFragment;
import com.example.bledemo.fragments.HomeFragment;
import com.example.bledemo.fragments.LogFragment;
import com.example.bledemo.fragments.OnDeviceSelectedInterface;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BLEManagerCallerInterface, OnDeviceSelectedInterface {

    public BLEManager bleManager;
    private MainActivity mainActivity;
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new LogFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private BluetoothGatt lastBluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm.beginTransaction().replace(R.id.main_container, fragment1).commit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_stop_scan_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bleManager != null) {
                    Log.d("VM", "scanning started");
                    bleManager.scanDevices();
                }
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fm.beginTransaction().replace(R.id.main_container, fragment1).commit();
                        active = fragment1;
                        return true;

                    case R.id.action_log:
                        fm.beginTransaction().replace(R.id.main_container, fragment2).commit();
                        active = fragment2;
                        return true;

                }
                return false;
            }
        });

        bleManager = new BLEManager(this, this);
        if (!bleManager.isBluetoothOn()) {
            bleManager.RequestBluetoothDeviceEnable(this);
        } else {
            bleManager.requestLocationPermissions(this, 1002);
        }
        mainActivity = this;
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

        boolean allPermissionsGranted = true;
        if (requestCode == 1002) {
            for (int currentResult : grantResults
            ) {
                if (currentResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
        try {
            if (requestCode == 1001) {
                if (resultCode != Activity.RESULT_OK) {

                } else {
                    bleManager.requestLocationPermissions(this, 1002);

                }
            }

        } catch (Exception error) {

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
                try {
                    ListView listView = (ListView) findViewById(R.id.devices_list_id);
                    BluetoothDeviceListAdapter adapter = new BluetoothDeviceListAdapter(getApplicationContext(), bleManager.scanResults, mainActivity,mainActivity);
                    listView.setAdapter(adapter);

                } catch (Exception error) {

                }

            }
        });


    }

    @Override
    public void servicesDiscovered(final BluetoothGatt bg) {
        this.lastBluetoothGatt = bg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DeviceDetailFragment deviceDetailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentByTag("device");
                deviceDetailFragment.initListData(bg.getServices());
            }
        });

    }

    @Override
    public void characteristicChanged(final String bc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity, ""+bc, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeviceSelected(final String address, final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment newFragment = new DeviceDetailFragment();
                Bundle args = new Bundle();
                args.putString("deviceAddress", address);
                args.putString("deviceName", name);

                newFragment.setArguments(args);

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(active);
                transaction.add(R.id.main_container, newFragment, "device");
                transaction.addToBackStack(null);
                transaction.commit();
                active = newFragment;
            }
        });
    }

    @Override
    public void connectToGatServer(String address) {
        bleManager.connectToGATTServer(mainActivity.bleManager.getByAddress(address));
    }
}
