package com.example.bledemo.broadcast;

import android.os.Bundle;

public interface BroadcastManagerCallerInterface {

    void MessageReceivedThroughBroadcastManager(
            String channel, String type, Bundle message);

    void ErrorAtBroadcastManager(Exception error);
}
