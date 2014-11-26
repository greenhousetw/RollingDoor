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
import com.bosswiin.SecurityLocker.R;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapperUiCallbacks;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * BossWiinBlueToothManager
 * This class provides bluetooth device collection access and action transmition
 */
public class JBluetoothManager implements INotificationHandler {

    // value for enable bluetooth hardware
    private static final int                                    REQUEST_ENABLE_BT       = 1;
    // timeout value for scan
    private final        double                                 mScanningTimeoutSeconds = 5;
    // string for loggin
    private final        String                                 mLogTag                 = JBluetoothManager.class.getName();
    // wrapper for ble operations
    private              BleWrapper                             mBleWrapper             = null;
    // the storage for bluetooth devices
    private              LinkedHashMap<String, BluetoothDevice> bluetoothDeviceList     = new LinkedHashMap<String, BluetoothDevice>();
    // context of activity
    private              Context                                context                 = null;
    // for thread using
    private              Handler                                mHandler                = null;
    // flag for in scanning
    private              boolean                                mScanning               = false;
    // scannin timeout
    private              Runnable                               mTimeout                = null;
    // action head of BLEAction
    private              BLEActionBase                          mBleAction              = null;
    // call back instance
    private              IJBTManagerUICallback                  mJBTUICallBack          = null;

    /**
     * Initializes a new instance of the BossWiinBlueToothManager class.
     * date: 2014/10/23
     *
     * @param context to use to open or create the database
     * @author Yu-Hua Tseng
     */
    public JBluetoothManager(Context context) {
        this.context = context;
    }

    /**
     * To stop monitoring rssi
     * date: 2014/11/13
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean stopMonitoringRSSI() {

        boolean result = false;

        if (this.mBleWrapper != null) {
            this.mBleWrapper.stopMonitoringRssiValue();
            result = true;
        }

        return result;
    }

    /**
     * check connection is alive
     * date: 2014/11/26
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean isConnected() {

        boolean result = false;

        if (this.mBleWrapper != null) {
            result = this.mBleWrapper.isConnected();
        }

        return result;
    }

    /**
     * To disconnect from remote peripheral
     * date: 2014/11/13
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean disconnect() {

        boolean result = false;

        if (this.mBleWrapper != null) {
            this.mBleWrapper.diconnect();
            result = true;
        }

        return result;
    }

    /**
     * To close connection
     * date: 2014/11/13
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean closeConnection() {
        boolean result = false;

        if (this.mBleWrapper != null) {
            this.mBleWrapper.close();
            result = true;
        }

        return result;
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
    public boolean setBluetoothLowEnergyWrapper(final IJBTManagerUICallback interfaceDeviceFound) {
        boolean result = false;

        if (interfaceDeviceFound == null) {
            throw new IllegalArgumentException("orz! interfaceDeviceFound is null");
        }

        this.mJBTUICallBack = interfaceDeviceFound;

        if (this.mBleWrapper == null) {
            this.mBleWrapper = new BleWrapper((Activity) this.context, new BleWrapperUiCallbacks.Null() {
                @Override
                public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                    interfaceDeviceFound.addNewDevices(device.getName(), device.getAddress(), rssi, record);
                    addDeviceIntoInternalList(device.getAddress(), device);
                }

                @Override
                public void uiNewValueForCharacteristic(BluetoothGatt gatt,
                                                        BluetoothDevice device, BluetoothGattService service,
                                                        BluetoothGattCharacteristic ch, String strValue, int intValue,
                                                        byte[] rawValue, String timestamp) {
                    handleNotification(gatt, device, service, ch, strValue, intValue, rawValue, timestamp);
                }

                @Override
                public void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi) {
                    handleNewRssiAvailable(gatt, device, rssi);
                }

                @Override
                public void uiDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
                    handleDeviceDisconnected(gatt, device);
                }
            });
        }

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

        BLEAcionEnum action = request.actionEnum;

        if (mBleWrapper != null) {
            request.context = this.context;
            request.bluetoothDevice = this.bluetoothDeviceList.get(request.remoteAddress);
            request.bleWrapper = this.mBleWrapper;

            if (this.connectToService(request)) {

                result = this.mBleAction.execute(request);
            }
        }
        else {
            Log.e(this.mLogTag, "orz! mBleWrapper is null");
        }

        return result;
    }

    /**
     * Change target device
     * date: 2014/11/09
     *
     * @param request instance of BLERequest
     * @author Yu-Hua Tseng
     */
    public void setNotification(BLERequest request) {

        // clean current connection
        //this.resetBLEWrapper(request.remoteAddress);

        // start to connect to the given peripheral
        if (request.actionEnum.equals(BLEAcionEnum.Notification)) {
            if (this.connectToService(request)) {
                request.handler = this;
                if (this.mBleWrapper.isConnected()) {
                    this.executeRequest(request);
                }
            }
        }
    }

    /**
     * To register notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param bluetoothGattCharacteristic instance of BluetoothGattCharacteristic
     * @author Yu-Hua Tseng
     */
    @Override
    public void registerNotification(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.mBleWrapper.requestCharacteristicValue(bluetoothGattCharacteristic);
    }

    /**
     * This method should notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param gatt      instance of BluetoothGatt
     * @param device    instance of BluetoothDevice
     * @param service   instance of BluetoothGattService
     * @param ch        instance of BluetoothGattCharacteristic
     * @param strValue  the data is stored in remote peripheral, type in string
     * @param intValue  the data is stored in remote peripheral, type in int
     * @param rawValue  the data is stored in remote peripheral, type in byte
     * @param timestamp time stamp for receiving
     * @author Yu-Hua Tseng
     */
    @Override
    public void handleNotification(BluetoothGatt gatt,
                                   BluetoothDevice device, BluetoothGattService service,
                                   BluetoothGattCharacteristic ch, final String strValue, int intValue,
                                   byte[] rawValue, final String timestamp) {

        Activity activity = ((Activity) this.context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mJBTUICallBack.passContentToActivity(strValue);
            }
        });
    }

    /**
     * process new Rssi value
     * date: 2014/11/14
     *
     * @param gatt   instance of BluetoothGatt
     * @param device instance of BluetoothDevice
     * @param rssi   strength of signal
     * @author Yu-Hua Tseng
     */
    @Override
    public void handleNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi) {
        final Activity activity = ((Activity) this.context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String rssiValue = activity.getString(R.string.rssiPrefixValue) + Integer.toString(rssi);
                mJBTUICallBack.passContentToActivity(rssiValue);
            }
        });
    }

    /**
     * process new Rssi value
     * date: 2014/11/14
     *
     * @param gatt   instance of BluetoothGatt
     * @param device instance of BluetoothDevice
     * @author Yu-Hua Tseng
     */
    @Override
    public void handleDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device) {
        final Activity activity = ((Activity) this.context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mJBTUICallBack.passContentToActivity(activity.getString(R.string.connectionStatus));
            }
        });

        gatt.close();
        gatt.disconnect();
        this.mBleWrapper.resetBlueToothDevice();
    }

    /**
     * Reset connection
     * date: 2014/11/18
     *
     * @param selectedAddress the target device's address
     * @author Yu-Hua Tseng
     */
    public void resetConnection(String selectedAddress) {
        // shutdown automatic scanning
        //this.stopScanning();

        try {
            if (selectedAddress.length() != 0) {
                this.stopMonitoringRSSI();
                this.disconnect();
                this.closeConnection();
                this.resetBLEWrapper(selectedAddress);
            }
        } catch (Exception ex) {
            Log.w(this.mLogTag, "reset connection");
        }
    }

    /**
     * reset instance of BLE peripheral
     * date: 2014/10/31
     *
     * @param remoteAddress address of remote peripheral
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean resetBLEWrapper(String remoteAddress) {

        boolean result = false;

        try {
            this.mBleWrapper.resetWrapperData();
            this.mBleWrapper.resetBlueToothDevice();
            result = true;
        } catch (Exception ex) {
            Log.e(JBluetoothManager.class.getName(), ex.getMessage());
        }

        return result;
    }

    /**
     * This method should notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/14
     *
     * @param address address that we want to check
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean connectToPeripheral(String address) {

        boolean isConnected = false;

        if (address != null && address.length() != 0 && this.mBleWrapper != null) {

            try {

                int retryTimes = 5;
                double waitSeconds = 0.2;

                while (retryTimes > 0) {

                    if (retryTimes == 0) {
                        break;
                    }

                    if (this.mBleWrapper.connect(address)) {
                        if (this.mBleWrapper.getDevice().getAddress() == address && this.mBleWrapper.isConnected()) {
                            this.addDeviceIntoInternalList(address, this.mBleWrapper.getDevice());
                            Thread.sleep((long) CommonHelper.SecsToMilliSeconds(waitSeconds));

                            isConnected = this.mBleWrapper.isConnected();
                            Log.e(mLogTag, "successfully connect to " + address);
                            break;
                        }
                    }

                    Thread.sleep((long) CommonHelper.SecsToMilliSeconds(waitSeconds));
                    retryTimes--;
                }
            } catch (Exception ex) {
                Log.e(this.mLogTag, ex.getMessage());
            }
        }
        else {
            Log.e(mLogTag, "wrapper instance or the value of address is empty");
        }

        return isConnected;
    }

    /**
     * To add device into internal list for management
     * date: 2014/11/18
     *
     * @param device  instance of BluetoothDevice
     * @param address address that we want to check
     * @author Yu-Hua Tseng
     */
    private void addDeviceIntoInternalList(String address, BluetoothDevice device) {

        if (!CommonHelper.stringIsNullOrEmpty(address) && !this.bluetoothDeviceList.containsKey(address)) {
            this.bluetoothDeviceList.put(address, device);
        }
    }

    /**
     * set ble action chain
     * date: 2014/10/24
     *
     * @author Yu-Hua Tseng
     */
    private void setCommandChain() {

        //this.mBleAction = new BLEActionSend();
        this.mBleAction = new BLEActionChungJeSend();
        this.mBleAction.setSuccessor(new BLEActionNotificaiton());
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

    /**
     * Connect to the specific service
     * date: 2014/11/09
     *
     * @param request instance of BLERequest
     * @author Yu-Hua Tseng
     */
    private boolean connectToService(BLERequest request) {
        int retryTimes = 10;
        double waitSeconds = 1;
        boolean isConnect = false;

        while (!isConnect) {
            isConnect = this.mBleWrapper.connect(request.remoteAddress);
            if (retryTimes == 0 || isConnect) {
                break;
            }

            try {
                Thread.sleep((int) CommonHelper.SecsToMilliSeconds(waitSeconds));
            } catch (Exception ex) {
                Log.e(mLogTag, ex.getMessage());
            }
            retryTimes--;
        }

        if (isConnect) {

            isConnect = false;
            this.mBleWrapper.startServicesDiscovery();
            retryTimes = 10;
            // discover services for 10 seconds
            while (!this.mBleWrapper.isServiceDiscvoeryDone) {

                if (retryTimes == 0) {
                    break;
                }

                try {
                    Thread.sleep((int) CommonHelper.SecsToMilliSeconds(waitSeconds));
                } catch (Exception ex) {
                    Log.e(mLogTag, ex.getMessage());
                }

                retryTimes--;
            }

            if (this.mBleWrapper.isServiceDiscvoeryDone) {
                for (BluetoothGattService service : this.mBleWrapper.getCachedServices()) {
                    if (service.getUuid().equals(UUID.fromString(request.serviceUUID))) {
                        request.targetService = service;
                        isConnect = true;
                        break;
                    }
                }
            }
        }

        return isConnect;
    }
}
