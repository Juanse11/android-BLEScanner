package com.example.bledemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
        String payload=intent.getExtras().getString("payload");
        String type=intent.getExtras().getString("type");
        caller.
                MessageReceivedThroughBroadcastManager(
                        this.channel,type,payload);
    }

    public void sendBroadcast(String type,String message){
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
