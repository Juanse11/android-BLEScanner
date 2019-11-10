package com.example.bledemo.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bledemo.R;
import com.example.bledemo.adapters.BluetoothDeviceListAdapter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLEManager extends ScanCallback {
    BLEManagerCallerInterface caller;
    Context context;

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    public List<ScanResult> scanResults = new ArrayList<>();
    static int REQUEST_BLUETOOTH_PERMISSION_NEEDED = 1001;
    private BluetoothGatt lastBluetoothGatt;
    UUID HEART_RATE_SERVICE_UUID;
    UUID HEART_RATE_MEASUREMENT_CHAR_UUID;
    UUID HEART_RATE_CONTROL_POINT_CHAR_UUID;
    UUID CLIENT_CHARACTERISTIC_CONFIG_UUID ;


    public BLEManager(BLEManagerCallerInterface caller, Context context) {
        this.caller = caller;
        this.context = context;
        initializeBluetoothManager();
        HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D);
        HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37);
        HEART_RATE_CONTROL_POINT_CHAR_UUID = convertFromInteger(0x2A39);
        CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902);
    }

    public void initializeBluetoothManager() {
        try {
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            this.bluetoothAdapter = bluetoothManager.getAdapter();
        } catch (Exception error) {

        }
    }

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public boolean isBluetoothOn() {
        try {
            return bluetoothManager.getAdapter().isEnabled();
        } catch (Exception error) {

        }
        return false;
    }

    public void requestLocationPermissions(final Activity activity, int REQUEST_CODE) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean gps_enabled = false;
                boolean network_enabled = false;

                LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                try {
                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                if (!((gps_enabled) || (network_enabled))) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("In order to BLE connection be successful please proceed to enable the GPS")
                            .setTitle("Settings");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);

                        }
                    });

                    builder.create().show();
                }
            }
            if (ContextCompat.checkSelfPermission(this.context.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);

            }
        } catch (Exception error) {

        }

    }


    public void scanDevices() {
        try {
            scanResults.clear();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(this);
            caller.scanStartedSuccessfully();
        } catch (Exception error) {

        }
    }

    public static boolean CheckIfBLEIsSupportedOrNot(Context context) {
        try {
            if (!context.getPackageManager().
                    hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            }
            return true;
        } catch (Exception error) {

        }
        return false;
    }

    public static boolean RequestBluetoothDeviceEnable(final Activity activity) {
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity)
                        .setTitle("Bluetooth")
                        .setMessage("The bluetooth device must be enabled in order to connect the device")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                activity.startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_PERMISSION_NEEDED);
                            }
                        });
                builder.show();

            } else {
                return true;
            }
        } catch (Exception error) {

        }
        return false;
    }

    //How to know if a characteristic can be read, written, or can notify
    public static boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() &
                (BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    public static boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    public static boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0);
    }

    private void searchAndSetAllNotifyAbleCharacteristics() {
        try {
            if (lastBluetoothGatt != null) {
                for (BluetoothGattService currentService : lastBluetoothGatt.getServices()) {
                    if (currentService != null) {
                        for (BluetoothGattCharacteristic currentCharacteristic : currentService.getCharacteristics()) {
                            if (currentCharacteristic != null) {
                                if (isCharacteristicNotifiable(currentCharacteristic)) {
                                    try{
                                        BluetoothGattDescriptor desc = currentCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                                        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        lastBluetoothGatt.writeDescriptor(desc);
                                    }catch (Error e){
                                        Log.d("Error", e.toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception error) {
            Log.d("Error", "Error");
        }
    }

    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        try {
            if (characteristic == null) return false;
            return lastBluetoothGatt.readCharacteristic(characteristic);
        } catch (Exception error) {
        }
        return false;
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        try {
            if (characteristic == null) return false;
            characteristic.setValue(data);
            return lastBluetoothGatt.writeCharacteristic(characteristic);
        } catch (Exception error) {

        }
        return false;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (!isResultAlreadyAtList(result)) {
            scanResults.add(result);
            caller.newDeviceDetected();
        }

    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {

    }

    @Override
    public void onScanFailed(int errorCode) {
        caller.scanFailed(errorCode);
    }

    public boolean isResultAlreadyAtList(ScanResult newResult) {
        for (ScanResult current : scanResults) {
            if (current.getDevice().getAddress().equals(newResult.getDevice().getAddress())) {
                return true;
            }
        }
        return false;
    }

    public BluetoothDevice getByAddress(String targetAddress) {
        for (ScanResult current : scanResults) {
            if (current != null) {
                if (current.getDevice().getAddress().equals(targetAddress)) {
                    return current.getDevice();
                }
            }
        }
        return null;
    }

    public static String parse(final BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        String s = "";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            s = new String(data) + "\n" +
                    stringBuilder.toString();
        }
        return s;
    }

    public void connectToGATTServer(BluetoothDevice device) {
        try {

            device.connectGatt(this.context, false, new BluetoothGattCallback() {
                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt,
                                                    int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        caller.connectionStatus("Discovering services");
                        gatt.discoverServices();
                    }else{
                        caller.connectionToBleFailed();
                        caller.connectionStatus("Connection failed");
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    lastBluetoothGatt = gatt;
                    caller.connectionStatus("Setting characteristics up");
                    searchAndSetAllNotifyAbleCharacteristics();

                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);

                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    String v = parse(characteristic);
                    caller.characteristicChanged(v);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    Log.d("asd", "asdasd");
                    caller.servicesDiscovered(gatt);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }
            }, BluetoothDevice.TRANSPORT_LE);
        } catch (Exception error) {

        }
    }

}
