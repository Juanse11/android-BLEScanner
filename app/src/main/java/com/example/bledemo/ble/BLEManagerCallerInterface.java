package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();
    void servicesDiscovered(ArrayList<BluetoothGattService> bs);
    void characteristicChanged(String bc, BluetoothGattCharacteristic c);
    void characteristicWrite(String bc, BluetoothGattCharacteristic c);
    void characteristicRead(String bc, BluetoothGattCharacteristic c);
    void connectionToBleFailed();
    void connectionToBleSuccesfully();
    void connectionStatus(String status);

}
