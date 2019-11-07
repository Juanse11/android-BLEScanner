package com.example.bledemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateBroadcastManager extends BroadcastReceiver {

    private Context context;
    private String channel;
    private NetworkStateBroadcastManagerCallerInterface caller;

    public NetworkStateBroadcastManager(Context context,
                                        String channel,
                                        NetworkStateBroadcastManagerCallerInterface caller) {
        this.context = context;
        this.channel = channel;
        this.caller = caller;
        initializeBroadcast();
    }

    public void initializeBroadcast() {
        try {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this, intentFilter);
        } catch (Exception error) {
            caller.ErrorAtBroadcastManager(error);
        }
    }

    public void unRegister() {
        try {
            context.unregisterReceiver(this);
        } catch (Exception error) {
            caller.ErrorAtBroadcastManager(error);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            Log.d("NetworkBroadcast", "ONLINE");
            caller.onNetworkStatusChange("STATUS", "ONLINE");
        } else {
            Log.d("NetworkBroadcast", "OFFLINE");
            caller.onNetworkStatusChange("STATUS", "OFFLINE");
        }


    }

}
