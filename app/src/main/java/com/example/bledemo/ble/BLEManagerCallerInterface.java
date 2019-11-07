package com.example.bledemo.ble;

import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();
    void servicesDiscovered(List<BluetoothGattService> services);

}
