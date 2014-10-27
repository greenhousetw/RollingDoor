package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;
import com.bosswiin.sharelibs.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by 9708023 on 2014/10/22.
 */
public class BossWiinBlueToothManager {

    private BLEActionBase openAction = null, closeAction = null, scanAction = null, stopScan = null, checkBLE = null;

    private BLEActionBase disConnect = null, sendAction = null;

    private Context context = null;

    private BleWrapper bleWrapper = null;

    private HashMap<String, BluetoothDevice> deviceMap = new HashMap<String, BluetoothDevice>();

    private BossWiinBlueToothManager btManager=this;

    public BossWiinBlueToothManager() {

        this.context = ContextHelper.GetGlobalContext();

        this.bleWrapper = new BleWrapper((Activity) this.context, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device,
                                      final int rssi,
                                      final byte[] record
            ) {
                String msg = "uiDeviceFound: " + device.getName() + ", " + rssi + "," + Integer.toString(rssi);
                Log.d("DEBUG", "uiDeviceFound: " + msg);
                btManager.PushData(device.getName(), device);
            }
        });

        if (!this.bleWrapper.checkBleHardwareAvailable()) {
            String errorMessage = "bluetooth device is not availabe";
            Log.e(this.getClass().getName(), errorMessage);
            throw new RuntimeException(errorMessage);
        }

        this.openAction = new BLEOpen();
        this.scanAction = new BLEScan();
        this.sendAction = new BLESend();
        this.stopScan = new BLEStopScan();
        this.checkBLE = new BLECheckEquipment();
        this.disConnect = new BLEDisconnect();
        this.closeAction = new BLEClose();

        this.openAction.SetSuccessor(this.scanAction);
        this.scanAction.SetSuccessor(this.sendAction);
        this.sendAction.SetSuccessor(this.checkBLE);
        this.checkBLE.SetSuccessor(this.stopScan);
        this.stopScan.SetSuccessor(this.disConnect);
        this.disConnect.SetSuccessor(this.closeAction);
    }

    public boolean Execute(BLERequest request) {
        request.SetWrapper(this.bleWrapper);
        return this.openAction.Execute(request);
    }


    public synchronized boolean PushData(String deviceName, BluetoothDevice device) {
        boolean result = false;

        if (this.deviceMap.containsKey(deviceName)) {
            this.deviceMap.remove(deviceName);
        }

        this.deviceMap.put(deviceName, device);

        result = true;

        return result;
    }

    public ArrayList<String> GetDeviceList() {
        ArrayList<String> deviceNameList = new ArrayList<String>();

        for (String name : this.deviceMap.keySet()) {
            deviceNameList.add(name);
        }

        return deviceNameList;
    }

    public BluetoothDevice GetDevice(String deviceName) {
        return this.deviceMap.get(deviceName);
    }
}
