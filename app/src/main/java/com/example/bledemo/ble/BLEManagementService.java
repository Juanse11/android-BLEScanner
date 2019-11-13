package com.example.bledemo.ble;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bledemo.MainActivity;
import com.example.bledemo.R;
import com.example.bledemo.broadcast.BroadcastManager;
import com.example.bledemo.broadcast.BroadcastManagerCallerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BLEManagementService extends Service implements BroadcastManagerCallerInterface, BLEManagerCallerInterface {

    private static final String CHANNEL_ID = "BLEServiceChannel";
    BroadcastManager broadcastManager;
    BLEManager bleManager;
    public static boolean isServiceRunning = false;
    public static final String BLE_SERVICE_CHANNEL = "com.example.bledemo.SOCKET_SERVICE_CHANNEL";
    public static final String START_SCANNING = "START_SCANNING";
    public static final String NEW_DEVICE_DETECTED = "NEW_DEVICE_DETECTED";
    public static final String CONNECT_TO_GATT_SERVER = "CONNECT_TO_GATT_SERVER";
    public static final String SERVICES_DISCOVERED = "SERVICES_DISCOVERED";
    public static final String READ_CHARACTERISTIC = "READ_CHARACTERISTIC";
    public static final String ON_READ_CHARACTERISTIC = "ON_READ_CHARACTERISTIC";
    public static final String CHANGE_CHARACTERISTIC = "CHANGE_CHARACTERISTIC";
    public static final String WRITE_CHARACTERISTIC = "WRITE_CHARACTERISTIC";
    public static final String ON_WRITE_CHARACTERISTIC = "ON_WRITE_CHARACTERISTIC";
    public static final String GET_LAST_STATE = "GET_LAST_STATE";
    public static final String GET_LAST_STATE_RESULT = "GET_LAST_STATE_RESULT";


    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_CONNECT = "com.example.myfirstapplication.network.action.ACTION_CONNECT";
    private static final String ACTION_BAZ = "com.example.myfirstapplication.network.action.BAZ";
    private int notificationID;

    // TODO: Rename parameters
    //private String SERVER_HOST = "172.17.9.21";
    //private int SERVER_PORT = 9090;


    private ArrayList<BluetoothGattService> gattServices;


    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        isServiceRunning = true;
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        Thread t = new Thread("BLEService(" + startId + ")") {
            @Override
            public void run() {
                if (intent != null) {
                    final String action = intent.getAction();
                    if (ACTION_CONNECT.equals(action)) {
                        initializeBroadcastManager();
                        initializeBLEManager();
                    }
                }
            }
        };
        t.start();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BLE Service Channel";
            String description = "BLE ServiceChannel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void initializeBroadcastManager() {
        try {
            if (broadcastManager == null) {
                broadcastManager = new BroadcastManager(
                        getApplicationContext(),
                        BLE_SERVICE_CHANNEL,
                        this);
            }
        } catch (Exception error) {

        }
    }

    private void initializeBLEManager() {
        try {
            if (bleManager == null) {
                bleManager = new BLEManager(this, this);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type, Bundle message) {
        try {
            switch (type) {
                case START_SCANNING:
                    bleManager.scanDevices();
                    break;
                case CONNECT_TO_GATT_SERVER:
                    bleManager.connectToGATTServer(bleManager.getByAddress(message.getString("deviceAddress")));
                    break;
                case GET_LAST_STATE:
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("services",(ArrayList<BluetoothGattService>) bleManager.lastBluetoothGatt.getServices());
                    b.putString("address", bleManager.lastBluetoothGatt.getDevice().getAddress());
                    b.putString("name", bleManager.lastBluetoothGatt.getDevice().getName());
                    broadcastManager.sendBroadcast(GET_LAST_STATE_RESULT, b);
                    break;
                case READ_CHARACTERISTIC:
                    String servUUID = message.getString("servUUID");
                    String charUUID = message.getString("charUUID");
                    bleManager.readCharacteristic(bleManager.lastBluetoothGatt.getService(UUID.fromString(servUUID))
                            .getCharacteristic(UUID.fromString(charUUID)));
                    break;
                case WRITE_CHARACTERISTIC:
                    String serviceUUID = message.getString("servUUID");
                    String characterUUID = message.getString("charUUID");
                    String value = message.getString("value");
                    bleManager.setValue(value, serviceUUID, characterUUID);
            }
        } catch (Exception error) {

        }

    }

    private void sendNotification(String charUUID) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bluetooth Low Energy")
                .setContentText("Characteristic with UUID " + charUUID + " has changed")
                .setSmallIcon(R.drawable.ic_bluetooth_searching_24px)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, notification.build());
    }

    @Override
    public void ErrorAtBroadcastManager(Exception error) {

    }

    @Override
    public void onDestroy() {
        if (broadcastManager != null) {
            broadcastManager.unRegister();
        }
        broadcastManager = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void scanStartedSuccessfully() {

    }

    @Override
    public void scanStoped() {

    }

    @Override
    public void scanFailed(int error) {

    }

    @Override
    public void newDeviceDetected() {
        Bundle b = new Bundle();
        b.putParcelableArrayList("scanResults", bleManager.scanResults);
        broadcastManager.sendBroadcast(NEW_DEVICE_DETECTED, b);
    }

    @Override
    public void servicesDiscovered(ArrayList<BluetoothGattService> bs) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("services", bs);
        broadcastManager.sendBroadcast(SERVICES_DISCOVERED, b);
    }

    @Override
    public void characteristicChanged(String bc, BluetoothGattCharacteristic c) {
        Bundle b = new Bundle();
        b.putParcelable("characteristic", c);
        b.putString("value", bc);
        broadcastManager.sendBroadcast(CHANGE_CHARACTERISTIC, b);
        sendNotification(c.getUuid().toString());
    }

    @Override
    public void characteristicWrite(String bc, BluetoothGattCharacteristic c) {
        Bundle b = new Bundle();
        b.putParcelable("characteristic", c);
        b.putString("value", bc);
        broadcastManager.sendBroadcast(ON_WRITE_CHARACTERISTIC, b);
    }

    @Override
    public void characteristicRead(String bc, BluetoothGattCharacteristic c) {
        Bundle b = new Bundle();
        b.putParcelable("characteristic", c);
        b.putString("value", bc);
        broadcastManager.sendBroadcast(BLEManagementService.ON_READ_CHARACTERISTIC, b);
    }


    @Override
    public void connectionToBleFailed() {

    }

    @Override
    public void connectionToBleSuccesfully() {

    }

    @Override
    public void connectionStatus(String status) {

    }


}
