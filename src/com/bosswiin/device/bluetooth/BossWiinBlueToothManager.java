/**
 * BossWiinBlueToothManager.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import com.bosswiin.device.bluetooth.blehandelr.BleNamesResolver;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;
import com.bosswiin.sharelibs.JSONHelper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * BossWiinBlueToothManager
 * This class provides bluetooth device collection access and action transmition
 */
public class BossWiinBlueToothManager {

    // log tag
    private final String logTag = this.getClass().getName();

    // action of open, close, scan, stop and check
    private BLEActionBase openAction = null, closeAction = null, scanAction = null, stopScan = null, checkBLE = null;

    // action of dis and send
    private BLEActionBase disConnect = null, sendAction = null;

    // instance of activity runtime context
    private Context context = null;

    // the wrapper for ble
    private BleWrapper bleWrapper = null;

    // collection of BluetoothDevice
    private HashMap<String, BLEDeviceInfo> deviceMap = new HashMap<String, BLEDeviceInfo>();

    // instance of self, which will be used by BLEWarpper for callback
    private BossWiinBlueToothManager btManager = this;

    /**
     * Initializes a new instance of the BossWiinBlueToothManager class.
     * date: 2014/10/23
     *
     * @param externalContext to use to open or create the database
     * @author Yu-Hua Tseng
     */
    public BossWiinBlueToothManager(Context externalContext) {

        this.context = externalContext;

        this.bleWrapper = new BleWrapper((Activity) this.context, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device,
                                      final int rssi,
                                      final byte[] record
            ) {
                btManager.PushData(device.getAddress(), Integer.toString(rssi), device);
            }
        });

        if (!this.bleWrapper.checkBleHardwareAvailable()) {
            String errorMessage = "bluetooth device is not availabe";
            Log.e(this.getClass().getName(), errorMessage);
            Toast.makeText(((Activity) this.context), "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
            ((Activity) this.context).finish();
        }

        this.openAction = new BLEActionOpen();
        this.scanAction = new BLEActionScan();
        this.sendAction = new BLEActionSend();
        this.stopScan = new BLEActionStopScan();
        this.checkBLE = new BLEActionCheckEquipment();
        this.disConnect = new BLEActionDisconnect();
        this.closeAction = new BLEActionClose();

        // set command chain
        this.openAction.SetSuccessor(this.scanAction);
        this.scanAction.SetSuccessor(this.sendAction);
        this.sendAction.SetSuccessor(this.checkBLE);
        this.checkBLE.SetSuccessor(this.stopScan);
        this.stopScan.SetSuccessor(this.disConnect);
        this.disConnect.SetSuccessor(this.closeAction);
    }

    /**
     * To execute the action.
     * date: 2014/10/24
     *
     * @param deviecAddress uuid of bluetooth device
     * @param action        action that this device will execute
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean Execute(String deviecAddress, BLEAcionEnum action) {

        boolean result = false;
        BLERequest request = null;

        if (this.deviceMap.containsKey(deviecAddress)) {
            Log.d(this.logTag, "BT device:" + deviecAddress + "will do" + action.toString());
            request = new BLERequest();
            request.actionEnum = action;
            request.bleWrapper = this.bleWrapper;
            request.bluetoothDevice = this.deviceMap.get(deviecAddress).bluetoothDevice;
        }

        if (request != null) {
            result = this.openAction.Execute(request);
        }
        else {
            Log.w(this.logTag, deviecAddress + " is not available");
        }

        return result;
    }

    /**
     * Insert BluetoothDevice into collection for future using
     * date: 2014/10/27
     *
     * @param address address of bluetooth peripheral
     * @param rssi    rssi of bluetooth peripheral
     * @param device  instance of bluetooth peripheral
     * @return true for successful and false for fail.
     * @author Yu-Hua Tseng
     */
    public synchronized boolean PushData(String address, String rssi, BluetoothDevice device) {
        boolean result = false;

        try {

            if (this.deviceMap.containsKey(address)) {
                this.deviceMap.remove(address);
            }

            BLEDeviceInfo bluetoothDeviceInfo = new BLEDeviceInfo();
            bluetoothDeviceInfo.deviceName = device.getName();
            bluetoothDeviceInfo.deviceAddress = device.getAddress();
            bluetoothDeviceInfo.rssi = rssi;
            bluetoothDeviceInfo.bluetoothDevice = device;

            Map<String, String> uuidMap = new HashMap<String, String>();

            ParcelUuid[] uuids = device.getUuids();

            int index = 0;

            for (ParcelUuid uuid : uuids) {
                String prefixKey = "uuid" + Integer.toString(index);
                uuidMap.put(prefixKey, uuid.getUuid().toString());
                ++index;
            }

            JSONObject uuidRecords = JSONHelper.getJSON(uuidMap);
            uuidMap.clear();
            bluetoothDeviceInfo.uuidList = uuidRecords;
            this.deviceMap.put(address, bluetoothDeviceInfo);
            result = true;
        } catch (Exception ex) {
            Log.e(this.logTag, ex.getMessage());
        }

        return result;
    }

    /**
     * To provide bluetooth device name list
     * date: 2014/10/27
     *
     * @return name array
     * @author Yu-Hua Tseng
     */
    public ArrayList<String> GetDeviceList() {

        ArrayList<String> deviceNameList = new ArrayList<String>();

        try {
            for (String address : this.deviceMap.keySet()) {
                BLEDeviceInfo deviceInfo = this.deviceMap.get(address);
                deviceNameList.add(deviceInfo.deviceName + "," + deviceInfo.deviceAddress);
            }
        } catch (Exception ex) {
            Log.e(this.logTag, ex.getMessage());
        }

        return deviceNameList;
    }

    /**
     * Get service name list
     * date: 2014/10/27
     *
     * @return instance of BluetoothDevice
     * @author Yu-Hua Tseng
     */
    public ArrayList<String> GetDeviceServiceList() {

        ArrayList<String> serviceNameList = new ArrayList<String>();

        if(this.bleWrapper.isConnected()) {
            for (BluetoothGattService service : this.bleWrapper.getCachedServices()) {
                serviceNameList.add(BleNamesResolver.resolveUuid(service.getUuid().toString()));
            }
        }else{
            Toast.makeText(this.context, "No connection", Toast.LENGTH_SHORT);
        }

        return serviceNameList;
    }

    /**
     * Get BluetoothDevice instance, according to the given deviceName
     * date: 2014/10/27
     *
     * @return instance of BluetoothDevice
     * @author Yu-Hua Tseng
     */
    public BluetoothDevice GetDevice(String address) {

        BluetoothDevice bluetoothDevice = null;

        if (this.deviceMap.containsKey(address)) {
            bluetoothDevice = this.deviceMap.get(address).bluetoothDevice;
        }

        return bluetoothDevice;
    }
}
