/**
 * JBluetoothManager.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.LinkedHashMap;

/**
 * BossWiinBlueToothManager
 * This class provides bluetooth device collection access and action transmition
 */
public class JBluetoothManager {

    // value for enable bluetooth hardware
    private static final int REQUEST_ENABLE_BT = 1;
    // timeout value for scan
    private final double mScanningTimeoutSeconds = 5;
    // string for loggin
    private final String mLogTag = JBluetoothManager.class.getName();
    // wrapper for ble operations
    private BleWrapper mBleWrapper = null;
    // the storage for bluetooth devices
    private LinkedHashMap<String, BluetoothDevice> bluetoothDeviceList = new LinkedHashMap<String, BluetoothDevice>();
    // context of activity
    private Context context = null;
    // for thread using
    private Handler mHandler = null;
    // flag for in scanning
    private boolean mScanning = false;
    // scannin timeout
    private Runnable mTimeout = null;

    // action head of BLEAction
    private BLEActionBase mBleAction = null;

    /**
     * Initializes a new instance of the BossWiinBlueToothManager class.
     * date: 2014/10/23
     *
     * @param context to use to open or create the database
     * @author Yu-Hua Tseng
     */
    public JBluetoothManager(Context context) {
        this.context = context;
        this.enableBluetoothHardware(context);
    }

    /**
     * To execute the action.
     * date: 2014/10/24
     *
     * @param context instance of Context
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean enableBluetoothHardware(Context context) {

        boolean result = false;

        BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (adapter != null && !adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        PackageManager pm = context.getPackageManager();
        result = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        return result;
    }

    /**
     * To initialize BleWrapper instance and call back method
     * date: 2014/10/24
     *
     * @param interfaceDeviceFound call back method instance
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean setBluetoothLowEnergyWrapper(final IDeviceFoundCallback interfaceDeviceFound) {
        boolean result = false;

        if (interfaceDeviceFound == null) {
            throw new IllegalArgumentException("orz! interfaceDeviceFound is null");
        }

        this.mBleWrapper = new BleWrapper((Activity) this.context, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                interfaceDeviceFound.addNewDevices(device.getName(), device.getAddress(), rssi, record);
                bluetoothDeviceList.put(device.getAddress(), device);
            }
        });

        if (this.mBleWrapper.initialize()) {
            Log.d(this.mLogTag, "BleWrapper initialization is successful");
            this.mHandler = new Handler();
            this.setCommandChain();
            result = true;
        }

        return result;
    }

    /**
     * start to scan bluetooth devices around user
     * date: 2014/10/24
     *
     * @author Yu-Hua Tseng
     */
    public void startScanning() {

        if (this.mBleWrapper == null) {
            throw new IllegalArgumentException("orz! BleWrapper is null");
        }

        this.mScanning = true;
        this.addScanningTimeout();
        this.mBleWrapper.startScanning();
    }

    /**
     * start to scan bluetooth devices around user
     * date: 2014/10/24
     *
     * @author Yu-Hua Tseng
     */
    public void stopScanning() {

        if (this.mBleWrapper == null) {
            throw new IllegalArgumentException("orz! BleWrapper is null");
        }

        this.mBleWrapper.stopScanning();
        this.mScanning = false;

        try {
            Thread.sleep((long) CommonHelper.SecsToMilliSeconds(0.3));
            if (this.mHandler != null && this.mTimeout != null) {
                this.mHandler.removeCallbacks(this.mTimeout);
            }
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            Log.e(JBluetoothManager.class.getName(), errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        this.mTimeout = null;
    }

    /**
     * To execute the action.
     * date: 2014/10/31
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean executeRequest(BLERequest request) {

        boolean result = false;

        if (mBleWrapper != null) {
            request.context = this.context;
            request.bluetoothDevice = this.bluetoothDeviceList.get(request.remoteAddress);
            request.bleWrapper = this.mBleWrapper;
            result = this.mBleAction.execute(request);
        } else {
            Log.e(this.mLogTag, "orz! mBleWrapper is null");
        }

        return result;
    }

    public void changeBleDevice() {

        if (this.mBleWrapper.isConnected()) {
            this.mBleWrapper.diconnect();
            this.mBleWrapper.close();
        }

        if (this.mBleWrapper.getCachedServices() != null) {
            this.mBleWrapper.getCachedServices().clear();
        }

        BluetoothGattService gattService = this.mBleWrapper.getCachedService();
        BluetoothGatt gatt = this.mBleWrapper.getGatt();
        gattService = null;
        gatt = null;
    }

    /**
     * set ble action chain
     * date: 2014/10/24
     *
     * @author Yu-Hua Tseng
     */
    private void setCommandChain() {
        this.mBleAction = new BLEActionSend();
    }

    /**
     * make sure that potential scanning will take no longer
     * than scanning timeout seconds from now on
     * date: 2014/10/24
     *
     * @author Yu-Hua Tseng
     */
    private void addScanningTimeout() {

        this.mTimeout = new Runnable() {
            @Override
            public void run() {
                if (mBleWrapper == null) {
                    throw new IllegalArgumentException("orz! interfaceDeviceFound is null");
                }
                mScanning = false;
                mBleWrapper.stopScanning();
            }
        };

        this.mHandler.postDelayed(mTimeout, (long) CommonHelper.SecsToMilliSeconds(this.mScanningTimeoutSeconds));
    }
}
