package com.example.bledemo.broadcast;

public interface NetworkStateBroadcastManagerCallerInterface {

    void onNetworkStatusChange(String type, String message);

    void ErrorAtBroadcastManager(Exception error);
}
