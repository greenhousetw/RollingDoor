package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.*;
import android.content.*;
import android.os.Handler;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleNamesResolver;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by 9708023 on 2014/11/26.
 */
public class CJBLEHandler implements IBLEHandler {

    private final String mTAG = CJBLEHandler.class.getSimpleName();

    private static final int                             RSSI_UPDATE_TIME_INTERVAL = 1500; // 1.5 seconds
    private              Activity                        mParent                   = null;
    private              String                          mDeviceAddress            = "";
    private BluetoothManager mBluetoothManager         = null;
    private BluetoothAdapter mBluetoothAdapter         = null;
    private BluetoothDevice  mBluetoothDevice          = null;
    private              BluetoothGatt                   mBluetoothGatt            = null;
    private Handler mTimerHandler             = new Handler();
    private              boolean                         mTimerEnabled             = false;
    private boolean mConnected=false;

    private IJBTManagerUICallback mUICallback=null;
    private boolean isServiceDiscvoeryDone = false;

    /* callbacks called for any action on particular Ble Device */
    private final        BluetoothGattCallback           mBleCallback              = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // now we can start talking with the device, e.g.
                mBluetoothGatt.readRemoteRssi();

                // and we also want to get RSSI value to be updated periodically
                startMonitoringRssiValue();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnected = false;
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mUICallback.uiDeviceDisconnected(mBluetoothDevice.getAddress());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // now, when services discovery is finished, we can call getServices() for Gatt
                isServiceDiscvoeryDone = true;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String deviceName = gatt.getDevice().getName();
            String serviceName = BleNamesResolver.resolveServiceName(characteristic.getService().getUuid().toString().toLowerCase(Locale.getDefault()));
            String charName = BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString().toLowerCase(Locale.getDefault()));
            String description = "Device: " + deviceName + " Service: " + serviceName + " Characteristic: " + charName;

            // we got response regarding our request to write new value to the characteristic
            // let see if it failed or not
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mUICallback.uiWriteResult(mBluetoothDevice.getAddress(), characteristic.getUuid().toString(), description, true);
            }
            else {
                mUICallback.uiWriteResult(mBluetoothDevice.getAddress(), characteristic.getUuid().toString(), description + " STATUS = " + status, false);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // we got new value of RSSI of the connection, pass it to the UI
                mUICallback.uiNewRssiAvailable(mBluetoothDevice.getAddress(), rssi);
            }
        }
    };

    public CJBLEHandler(Activity activity, final IJBTManagerUICallback uiCallBackInstance) {
        this.mParent=activity;
        this.mUICallback=uiCallBackInstance;
    }

    /* initialize BLE and get BT Manager & Adapter */
    @Override
    public boolean initialize() {

        boolean result=false;

        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) this.mParent.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                result=false;
            }
        }

        if (this.mBluetoothAdapter == null){
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        }

        if (this.mBluetoothAdapter == null) {
            result=false;
        }

        result=true;

        return result;
    }

    @Override
    public boolean connect(String address){

        boolean result=false;

        this.isServiceDiscvoeryDone=false;
        if(this.mBluetoothGatt!=null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();

        }

        if (this.mBluetoothAdapter == null || address == null) {
            result=false;
        }

        this.mDeviceAddress=address;

        // check if we need to connect from scratch or just reconnect to previous device
        if (this.mBluetoothGatt != null && this.mBluetoothGatt.getDevice().getAddress().equals(address)) {
            // just reconnect
            result=this.mBluetoothGatt.connect();
        }
        else {
            // connect from scratch
            // get BluetoothDevice object for specified address
            this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(this.mDeviceAddress);
            if (mBluetoothDevice == null) {
                // we got wrong address - that device is not available!
                result=false;
            }

            // connect with remote device
            this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(this.mParent, false, this.mBleCallback);

            try {
                 if(this.mBluetoothGatt!=null) {
                    double waitSeconds=1;
                    int retryTimes=5;
                    this.mBluetoothGatt.discoverServices();
                    while(this.mBluetoothGatt.getServices().size()==0) {
                        if(retryTimes==0){
                            break;
                        }
                        this.mBluetoothGatt.discoverServices();
                        Thread.sleep((long)CommonHelper.SecsToMilliSeconds(waitSeconds));
                        retryTimes--;
                    }

                    if(this.mBluetoothGatt.getServices().size()>0){
                        this.mConnected=true;
                        result = true;
                    }
                }
            }catch (Exception ex) {
                Log.e(this.mTAG, ex.getMessage());
            }
        }

        return result;
    }

    @Override
    public void scanDevice() {

    }

    @Override
    public void writeData(UUID serviceName, UUID characteristics, byte[] data) {

        try {
            BluetoothGattService service= this.mBluetoothGatt.getService(serviceName);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristics);
            characteristic.setValue(data);
            this.mBluetoothGatt.writeCharacteristic(characteristic);
        }
        catch (Exception ex){
            Log.e(this.mTAG, ex.getMessage());
        }
    }

    /* request new RSSi value for the connection*/
    public void readPeriodicalyRssiValue(final boolean repeat) {
        mTimerEnabled = repeat;
        // check if we should stop checking RSSI value
        if (mConnected == false || mBluetoothGatt == null || mTimerEnabled == false) {
            mTimerEnabled = false;
            return;
        }

        this.mTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt == null ||
                        mBluetoothAdapter == null ||
                        mConnected == false) {
                    mTimerEnabled = false;
                    return;
                }

                // request RSSI value
                mBluetoothGatt.readRemoteRssi();
                // add call it once more in the future
                readPeriodicalyRssiValue(mTimerEnabled);
            }
        }, RSSI_UPDATE_TIME_INTERVAL);
    }

    /* starts monitoring RSSI value */
    public void startMonitoringRssiValue() {
        readPeriodicalyRssiValue(true);
    }

    private void displayData(String data) {
        if (data != null) {
            this.mUICallback.passContentToActivity(data);
        }
    }
}
