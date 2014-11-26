package com.bosswiin.device.bluetooth.ChwanJhe;

import android.app.Activity;
import android.bluetooth.*;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.bosswiin.device.bluetooth.IBLEHandler;
import com.bosswiin.device.bluetooth.IJBTManagerUICallback;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.UUID;

/**
 * Created by 9708023 on 2014/11/26.
 */
public class CJBLEHandler implements IBLEHandler {

    private final String mTAG = CJBLEHandler.class.getSimpleName();

    private              Activity                        mParent                   = null;
    private              String                          mDeviceAddress            = "";
    private BluetoothManager mBluetoothManager         = null;
    private BluetoothAdapter mBluetoothAdapter         = null;
    private BluetoothDevice mBluetoothDevice          = null;
    private              BluetoothGatt                   mBluetoothGatt            = null;
    private final        BluetoothGattCallback           mBleCallback=null;
    private IJBTManagerUICallback mUICallback=null;

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
    public boolean getGattService(BluetoothGattService gattService) {

        boolean result = false;

        try {
            if (gattService != null) {

                this.startReadRssi();
                result = true;
            }
        } catch (Exception ex) {
            Log.e(this.mTAG, ex.getMessage());
        }

        return result;
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

    @Override
    public void startReadRssi() {
    }

    private void displayData(String data) {
        if (data != null) {
            this.mUICallback.passContentToActivity(data);
        }
    }
}
