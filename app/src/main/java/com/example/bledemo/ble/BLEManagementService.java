package com.example.bledemo.ble;

import android.app.IntentService;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.widget.Toast;

import com.example.bledemo.broadcast.BroadcastManager;
import com.example.bledemo.broadcast.BroadcastManagerCallerInterface;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BLEManagementService extends IntentService implements BroadcastManagerCallerInterface, BLEManagerCallerInterface {

    BroadcastManager broadcastManager;
    BLEManager bleManager;
    public static String BLE_SERVICE_CHANNEL="com.example.bledemo.SOCKET_SERVICE_CHANNEL";
    public static String SERVER_TO_CLIENT_MESSAGE="SERVER_TO_CLIENT_MESSAGE";
    public static String CLIENT_TO_SERVER_MESSAGE="CLIENT_TO_SERVER_MESSAGE";
    public static String USER_CONNECTED="USER_CONNECTED";
    public static String USER_DISCONNECTED="USER_DISCONNECTED";
    public static String MESSAGE_SENT="MESSAGE_SENT";



    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_CONNECT = "com.example.myfirstapplication.network.action.ACTION_CONNECT";
    private static final String ACTION_BAZ = "com.example.myfirstapplication.network.action.BAZ";

    // TODO: Rename parameters
    //private String SERVER_HOST = "172.17.9.21";
    //private int SERVER_PORT = 9090;


    public BLEManagementService() {
        super("BLEManagementService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONNECT.equals(action)) {
                initializeBroadcastManager();
                initializeBLEManager();
            }

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void initializeBroadcastManager(){
        try{
            if(broadcastManager==null){
                broadcastManager=new BroadcastManager(
                        getApplicationContext(),
                        BLE_SERVICE_CHANNEL,
                        this);
            }
        }catch (Exception error){
            Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void initializeBLEManager() {
        try{
            if(bleManager == null){
                bleManager = new BLEManager(this,this);
            }
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type,String message) {
        try {
            /*if (clientSocketManager != null) {
                if (type.equals(CLIENT_TO_SERVER_MESSAGE)) {
                    clientSocketManager.sendMessage(message);
                }
            }*/
        }catch (Exception error){

        }

    }

    @Override
    public void ErrorAtBroadcastManager(Exception error) {

    }

    @Override
    public void onDestroy() {
        if(broadcastManager!=null){
            broadcastManager.unRegister();
        }
        broadcastManager=null;
        super.onDestroy();
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

    }

    @Override
    public void servicesDiscovered(BluetoothGatt bg) {

    }

    @Override
    public void characteristicChanged(String bc) {

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
