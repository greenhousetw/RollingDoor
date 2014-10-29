/**
 * BossWiinBlueToothManager.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.bosswiin.UserInterface.Components.BLEAdpaterBase;
import com.bosswiin.device.bluetooth.blehandelr.BleNamesResolver;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * BossWiinBlueToothManager
 * This class provides bluetooth device collection access and action transmition
 */
public class BossWiinBlueToothManager{

    // log tag
    private final String                           logTag     = this.getClass().getName();
    // instance of activity runtime context
    protected     Context                          context    = null;
    // instance of self, which will be used by BLEWarpper for callback
    protected     BossWiinBlueToothManager         btManager  = this;
    // the wrapper for ble
    private       BleWrapper                       bleWrapper = null;
    // collection of BluetoothDevice
    private       HashMap<String, BluetoothDevice> deviceMap  = new HashMap<String, BluetoothDevice>();
    // action of open, close, scan, stop and check
    private       BLEActionBase                    openAction = null, closeAction = null, scanAction = null, stopScan = null, checkBLE = null;
    // action of dis and send
    private BLEActionBase disConnect = null, sendAction = null;
    // handler for thread processing
    private Handler        threadHandler  = new Handler();
    // timeout value for scanning
    private double         timeoutSeconds = 8;
    // Adapter for UI operation
    private BLEAdpaterBase uiAdapter      = null;

    /**
     * Initializes a new instance of the BossWiinBlueToothManager class.
     * date: 2014/10/23
     *
     * @param externalContext to use to open or create the database
     * @author Yu-Hua Tseng
     */
    public BossWiinBlueToothManager(Context externalContext, BLEAdpaterBase adpater) {

        this.context = externalContext;
        this.uiAdapter = adpater;

        this.bleWrapper = new BleWrapper((Activity) this.context, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                btManager.PushData(device.getAddress(), device);
                btManager.PassToAdapter(device, rssi, record);
            }
        });

        if (!this.bleWrapper.checkBleHardwareAvailable()) {
            String errorMessage = "bluetooth device is not availabe";
            Log.e(this.getClass().getName(), errorMessage);
            Toast.makeText(((Activity) this.context), "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
            ((Activity) this.context).finish();
        }

        this.bleWrapper.initialize();

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

        boolean isScanRelated = action.equals(BLEAcionEnum.Scan) || action.equals(BLEAcionEnum.StopScan) ? true : false;

        if (isScanRelated || this.deviceMap.containsKey(deviecAddress)) {
            Log.d(this.logTag, "BT device:" + deviecAddress + "will do" + action.toString());
            request = new BLERequest();
            request.actionEnum = action;
            request.bleWrapper = this.bleWrapper;

            if (!isScanRelated) {
                request.bluetoothDevice = this.deviceMap.get(deviecAddress);
            }
        }

        if (request != null) {

            if (action.equals(BLEAcionEnum.Scan)) {
                this.StartScanningTimeout();
            }

            if(request.bleWrapper.isConnected()) {
                request.bleWrapper.diconnect();
            }

            result = this.openAction.Execute(request);
        }
        else {
            Log.w(this.logTag, deviecAddress + " is not available");
        }

        return result;
    }

    /**
     * This method will notify the Activity that bundles with Manager
     * date: 2014/10/29
     *
     * @param device instance of bluetooth device
     * @param rssi   signal strength of this instance
     * @param record records of the BLE device
     * @author Yu-Hua Tseng
     */
    public void PassToAdapter(final BluetoothDevice device, final int rssi, final byte[] record) {

        ((Activity) this.context).runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        uiAdapter.AddNewDevice(device, rssi, record);
                        // start to update UI
                        uiAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    /**
     * Insert BluetoothDevice into collection for future using
     * date: 2014/10/27
     *
     * @param address address of bluetooth peripheral
     * @param device  instance of bluetooth peripheral
     * @return true for successful and false for fail.
     * @author Yu-Hua Tseng
     */
    public synchronized boolean PushData(String address, BluetoothDevice device) {
        boolean result = false;

        try {

            if (this.deviceMap.containsKey(address)) {
                this.deviceMap.remove(address);
            }

            this.deviceMap.put(address, device);
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
                BluetoothDevice deviceInfo = this.deviceMap.get(address);
                deviceNameList.add(deviceInfo.getName() + "," + deviceInfo.getAddress());
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

        if (this.bleWrapper.isConnected()) {
            for (BluetoothGattService service : this.bleWrapper.getCachedServices()) {
                serviceNameList.add(BleNamesResolver.resolveUuid(service.getUuid().toString()));
            }
        }
        else {
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
            bluetoothDevice = this.deviceMap.get(address);
        }

        return bluetoothDevice;
    }

    /**
     * Stop scanning as timeout value reaching
     * date: 2014/10/29
     *
     * @author Yu-Hua Tseng
     */
    private void StartScanningTimeout() {

        Runnable timeoutExecutor = new Runnable() {
            @Override
            public void run() {
                if (btManager.bleWrapper == null) {
                    return;
                }

                btManager.Execute("", BLEAcionEnum.StopScan);
            }
        };

        Log.d(this.logTag, "Stop scanning in " + this.timeoutSeconds + " seconds");
        this.threadHandler.postDelayed(timeoutExecutor, (long) CommonHelper.SecsToMilliSeconds(this.timeoutSeconds));
    }
}
