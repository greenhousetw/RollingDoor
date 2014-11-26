package com.bosswiin.device.bluetooth.ChwanJhe;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.bosswiin.device.bluetooth.IBLEHandler;
import com.bosswiin.device.bluetooth.IJBTManagerUICallback;

/**
 * Created by 9708023 on 2014/11/26.
 */
public class CJBLEHandler implements IBLEHandler {

    private final String mTAG = CJBLEHandler.class.getSimpleName();

    private CJService mBluetoothLeService;

    private Activity acts = null;

    private ServiceConnection mServiceConnection = null;

    private BroadcastReceiver mGattUpdateReceiver = null;

    private IJBTManagerUICallback mUICallback = null;

    private boolean flag = false;

    private byte[] data = new byte[3];

    private BluetoothGattCharacteristic characteristicTx = null;

    public CJBLEHandler(Activity activity, IJBTManagerUICallback callBackInstance) {

        this.acts = activity;

        this.mUICallback = callBackInstance;

        this.mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName,
                                           IBinder service) {
                mBluetoothLeService = ((CJService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e(mTAG, "Unable to initialize Bluetooth");
                    acts.finish();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBluetoothLeService = null;
            }
        };

        this.mGattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (CJService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    Toast.makeText(acts.getApplicationContext(), "Disconnected",
                            Toast.LENGTH_SHORT).show();
                }
                else if (CJService.ACTION_GATT_SERVICES_DISCOVERED
                        .equals(action)) {
                    Toast.makeText(acts.getApplicationContext(), "Connected",
                            Toast.LENGTH_SHORT).show();

                    getGattService(mBluetoothLeService.getSupportedGattService());
                }
                else if (CJService.ACTION_DATA_AVAILABLE.equals(action)) {
                    data = intent.getByteArrayExtra(CJService.EXTRA_DATA);
                }
                else if (CJService.ACTION_GATT_RSSI.equals(action)) {
                    displayData(intent.getStringExtra(CJService.EXTRA_DATA));
                }
            }
        };
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(CJService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(CJService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(CJService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(CJService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(CJService.ACTION_GATT_RSSI);

        return intentFilter;
    }

    @Override
    public void setRegisterReceiver() {
        this.acts.registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean stopregisterReceiver() {

        boolean result = false;

        try {
            this.flag = false;
            this.acts.unregisterReceiver(this.mGattUpdateReceiver);
            result = true;
        } catch (Exception ex) {
            Log.e(this.mTAG, ex.getMessage());
        }

        return result;
    }

    @Override
    public void scanDevice() {

    }

    @Override
    public boolean getGattService(BluetoothGattService gattService) {

        boolean result = false;

        try {
            if (gattService != null) {

                this.startReadRssi();
                characteristicTx = gattService.getCharacteristic(CJService.UUID_BLE_SHIELD_TX);
                BluetoothGattCharacteristic characteristicRx = gattService.getCharacteristic(CJService.UUID_BLE_SHIELD_RX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRx,true);
                mBluetoothLeService.readCharacteristic(characteristicRx);
                result = true;
            }
        } catch (Exception ex) {
            Log.e(this.mTAG, ex.getMessage());
        }

        return result;
    }

    @Override
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void startReadRssi() {
        new Thread() {
            public void run() {

                while (flag) {
                    mBluetoothLeService.readRssi();
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    private void displayData(String data) {
        if (data != null) {
            this.mUICallback.passContentToActivity(data);
        }
    }
}
