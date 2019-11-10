package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();
    void servicesDiscovered(BluetoothGatt bg);
    void characteristicChanged(String bc);
    void connectionToBleFailed();
    void connectionToBleSuccesfully();
    void connectionStatus(String status);

}
