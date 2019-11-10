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
import com.example.bledemo.fragments.OnDeviceSelectedInterface;

import java.util.List;


public class BluetoothDeviceListAdapter extends ArrayAdapter<ScanResult> {
    private final Context context;
    private MainActivity mainActivity;
    private List<ScanResult> scanResultList;
    private OnDeviceSelectedInterface caller;

    public BluetoothDeviceListAdapter(@NonNull Context context, List<ScanResult> scanResultList, MainActivity mainActivity, OnDeviceSelectedInterface caller) {
        super(context, R.layout.device_list_item, scanResultList);
        this.context = context;
        this.mainActivity = mainActivity;
        this.scanResultList = scanResultList;
        this.caller = caller;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.device_list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.device_list_item_text_view);
        txtTitle.setText(scanResultList.get(position).getDevice().getAddress() + "");
        String deviceName = scanResultList.get(position).getDevice().getName();
        final TextView deviceNameTxtView = (TextView) rowView.findViewById(R.id.device_list_item_text_view2);
        deviceNameTxtView.setText(deviceName);

        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = ((TextView) view.findViewById(R.id.device_list_item_text_view)).getText() + "";
                Toast.makeText(context, "selected address: " + address, Toast.LENGTH_LONG).show();
                caller.onDeviceSelected(address, deviceNameTxtView.getText() + "");
            }
        });

        return rowView;
    }

}