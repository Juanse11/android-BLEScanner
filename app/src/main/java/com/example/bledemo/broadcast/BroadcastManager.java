package com.example.bledemo.broadcast;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.bledemo.R;

public class BroadcastManager extends BroadcastReceiver {

    private Context context;
    private String channel;
    private BroadcastManagerCallerInterface caller;

    public BroadcastManager(Context context,
                            String channel,
                            BroadcastManagerCallerInterface caller) {
        this.context = context;
        this.channel = channel;
        this.caller = caller;
        initializeBroadcast();
    }

    public void initializeBroadcast(){
        try{
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction(channel);
            context.registerReceiver(this,intentFilter);
        }catch (Exception error){
            caller.ErrorAtBroadcastManager(error);
        }
    }

    public void unRegister(){
        try{
            context.unregisterReceiver(this);
        }catch (Exception error){
            caller.ErrorAtBroadcastManager(error);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle payload=intent.getBundleExtra("payload");
        String type=intent.getExtras().getString("type");
        caller.
                MessageReceivedThroughBroadcastManager(
                        this.channel,type,payload);
    }

    public void sendBroadcast(String type, Bundle message){
        try{
            Intent intentToBesent=new Intent();
            intentToBesent.setAction(channel);
            intentToBesent.putExtra("payload",message);
            intentToBesent.putExtra("type",type);
            context.sendBroadcast(intentToBesent);
        }catch (Exception error){
            caller.ErrorAtBroadcastManager(error);
        }
    }




}
