package com.example.bledemo.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bledemo.MainActivity;
import com.example.bledemo.R;
import com.example.bledemo.ble.BLEManager;

import java.util.List;



public class CharacteristicListAdapter extends ArrayAdapter<BluetoothGattCharacteristic> {
    private final Context context;
    private List<BluetoothGattCharacteristic> scanResultList;

    public CharacteristicListAdapter(@NonNull Context context, List<BluetoothGattCharacteristic> scanResultList) {
        super(context, R.layout.device_list_item,scanResultList);
        this.context = context;
        this.scanResultList = scanResultList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null){
            convertView = inflater.inflate(R.layout.characteristic_list_item, parent, false);
        }

        return convertView;
    }
}