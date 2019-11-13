package com.example.bledemo.fragments;

import android.bluetooth.BluetoothGattCharacteristic;

public interface OnCharacteristicSelectedInterface {
    void onCharacteristicSelected(BluetoothGattCharacteristic characteristic);
    void onValueSet(String value, BluetoothGattCharacteristic characteristic);
    void getCharacteristic(BluetoothGattCharacteristic characteristic);
}
