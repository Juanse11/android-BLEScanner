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
import com.example.bledemo.fragments.CharacteristicDetailFragment;
import com.example.bledemo.fragments.DeviceDetailFragment;
import com.example.bledemo.fragments.HomeFragment;
import com.example.bledemo.fragments.LogFragment;
import com.example.bledemo.fragments.OnCharacteristicSelectedInterface;
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
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BLEManagerCallerInterface, OnDeviceSelectedInterface, OnCharacteristicSelectedInterface {

    public BLEManager bleManager;
    private MainActivity mainActivity;
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new LogFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    Fragment currentDevice;
    private BluetoothGatt lastBluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "log").hide(fragment2).commit();
        fm.executePendingTransactions();
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
                        if (active instanceof DeviceDetailFragment) {
                            fm.beginTransaction().remove(active).show(fragment1).commit();
                        } else if (active instanceof CharacteristicDetailFragment) {
                            fm.beginTransaction().remove(active).show(currentDevice).commit();
                        } else {
                            fm.beginTransaction().hide(active).show(fragment1).commit();
                        }
                        active = fragment1;

                        return true;

                    case R.id.action_log:
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        writeEntry("");
                        return true;

                }
                return false;
            }
        });

        bleManager = new BLEManager(this, this);
        if (!bleManager.isBluetoothOn()) {
            writeEntry("Bluetooth State: Off");
            bleManager.RequestBluetoothDeviceEnable(this);
        } else {
            writeEntry("Bluetooth State: On");
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
    public void onBackPressed() {
        if (active instanceof DeviceDetailFragment) {
            fm.beginTransaction().remove(active).show(fragment1).commit();
            active = fragment1;
        }
        if (active instanceof CharacteristicDetailFragment) {
            fm.beginTransaction().remove(active).show(currentDevice).commit();
            active = currentDevice;
        }
        super.onBackPressed();
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
                } else {
                    writeEntry("Location permissions granted");
                }
            }
            if (!allPermissionsGranted) {
                writeEntry("Location permissions not granted");
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
                    writeEntry("Bluetooth State: On");
                } else {
                    bleManager.requestLocationPermissions(this, 1002);

                }
            }

        } catch (Exception error) {

        }
    }

    @Override
    public void scanStartedSuccessfully() {
        writeEntry("Scanning devices...");

    }

    @Override
    public void scanStoped() {
        writeEntry("Scanning devices stopped");
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
                    writeEntry("New device detected nearby");
                    ListView listView = (ListView) findViewById(R.id.devices_list_id);
                    BluetoothDeviceListAdapter adapter = new BluetoothDeviceListAdapter(getApplicationContext(), bleManager.scanResults, mainActivity, mainActivity);
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
                writeEntry("Services discovered");
                DeviceDetailFragment deviceDetailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentByTag("device");
                deviceDetailFragment.initListData(bg.getServices());
            }
        });

    }

    @Override
    public void characteristicChanged(final String bc, final BluetoothGattCharacteristic c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((active instanceof CharacteristicDetailFragment) && active.getArguments().getString("charUUID").equals(c.getUuid().toString())) {
                    ((CharacteristicDetailFragment) active).updateValue(bc);
                }
            }
        });
    }

    @Override
    public void characteristicRead(final String bc, final BluetoothGattCharacteristic c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CharacteristicDetailFragment) active).readValue(bc, c);
            }
        });
    }

    @Override
    public void characteristicWrite(final String bc, final BluetoothGattCharacteristic c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View contextView = findViewById(R.id.main_container);

                Snackbar.make(contextView, "Success" ,Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void connectionToBleFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fm.beginTransaction().remove(active).show(fragment1).commit();
                active = fragment1;
                fm.executePendingTransactions();
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("There was a problem connecting with this device");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public void connectionToBleSuccesfully() {
        writeEntry("BLE device connected successful");
    }

    @Override
    public void connectionStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                writeEntry(status);
                DeviceDetailFragment deviceDetailFragment = (DeviceDetailFragment) getSupportFragmentManager().findFragmentByTag("device");
                deviceDetailFragment.updateProgressBar(status);
            }
        });
    }

    public void writeEntry(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("log");
                if (log != "") {
                    logFragment.newLogEntry(log);
                } else {
                    logFragment.updateList();
                }
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
                transaction.commit();
                transaction.addToBackStack(null);
                active = newFragment;
                currentDevice = newFragment;
            }
        });
    }

    @Override
    public void connectToGatServer(String address) {
        writeEntry("Connecting to BLE device...");
        bleManager.connectToGATTServer(bleManager.getByAddress(address));
    }

    @Override
    public void onCharacteristicSelected(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment newFragment = new CharacteristicDetailFragment();
                Bundle args = new Bundle();
                args.putString("charUUID", characteristic.getUuid().toString());
                args.putString("servUUID", characteristic.getService().getUuid().toString());
                newFragment.setArguments(args);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(active);
                transaction.add(R.id.main_container, newFragment, "char");
                transaction.commit();
                transaction.addToBackStack(null);
                active = newFragment;
            }
        });
    }

    @Override
    public void onValueSet(String value, String servUUID, String charUUID) {
        bleManager.setValue(value, servUUID, charUUID);
    }

    @Override
    public void getCharacteristic(String servUUID, String charUUID) {
        bleManager.readCharacteristic(this.lastBluetoothGatt.getService(UUID.fromString(servUUID)).getCharacteristic(UUID.fromString(charUUID)));
    }


}
