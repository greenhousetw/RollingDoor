/**
 * CJBLEHandler.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleNamesResolver;
import com.bosswiin.sharelibs.CommonHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * CJBLEHandler
 * This class handles BLE device operations
 */
public class CJBLEHandler implements IBLEHandler {

    private static final int              RSSI_UPDATE_TIME_INTERVAL = 1500; // 1.5 seconds
    private final        String           mTAG                      = CJBLEHandler.class.getSimpleName();
    private              Activity         mParent                   = null;
    private              String           mDeviceAddress            = "";
    private              BluetoothManager mBluetoothManager         = null;
    private              BluetoothAdapter mBluetoothAdapter         = null;
    private              BluetoothDevice  mBluetoothDevice          = null;
    private              BluetoothGatt    mBluetoothGatt            = null;
    private              Handler          mTimerHandler             = new Handler();
    private              boolean          mTimerEnabled             = false;
    private              boolean          mConnected                = false;

    private IJBTManagerUICallback mUICallback            = null;
    private boolean               isServiceDiscvoeryDone = false;

    /**
     * callbacks called for any action on particular Ble Device
     */
    private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // now we can start talking with the device, e.g.
                mConnected = true;
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
                final int rssiValue = rssi;

                mParent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(mTAG, Integer.toString(rssiValue));
                        mUICallback.uiNewRssiAvailable(mBluetoothDevice.getAddress(), rssiValue);
                    }
                });
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            // we got response regarding our request to fetch characteristic value
            if (status == BluetoothGatt.GATT_SUCCESS) {
                getCharacteristicValue(characteristic.getService().getUuid(),
                        characteristic.getUuid());
            }
        }
    };

    /**
     * Initializes a new instance of the CJBLEHandler class.
     * date: 2014/11/27
     *
     * @param activity           activity that we want to communicate with
     * @param uiCallBackInstance method for callback
     * @author Yu-Hua Tseng
     */
    public CJBLEHandler(Activity activity, final IJBTManagerUICallback uiCallBackInstance) {
        this.mParent = activity;
        this.mUICallback = uiCallBackInstance;
    }

    /**
     * Initialize BLE and get BT Manager & Adapter
     * date: 2014/11/27
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean initializeBTAdapter() {

        boolean result = false;

        final int REQUEST_ENABLE_BT = 1;

        if (this.mBluetoothManager == null) {

            this.mBluetoothManager = (BluetoothManager) this.mParent.getSystemService(Context.BLUETOOTH_SERVICE);

            if (this.mBluetoothManager == null) {
                result = false;
            }
        }

        if (this.mBluetoothAdapter == null) {

            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();

            if (this.mBluetoothAdapter != null) {

                if (!this.mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    this.mParent.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                result = true;
            }
        }

        return result;
    }

    /**
     * connect to the target device
     * date: 2014/11/27
     *
     * @param address the address of remote device
     * @return true for successfully check service count is more than 0 and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean connect(String address) {

        boolean result = false;

        Log.d(this.mTAG, "reset GATT");

        this.isServiceDiscvoeryDone = false;
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();

        }

        if (this.mBluetoothAdapter == null || address == null) {
            result = false;
        }

        this.mDeviceAddress = address;

        // check if we need to connect from scratch or just reconnect to previous device
        if (this.mBluetoothGatt != null && this.mBluetoothGatt.getDevice().getAddress().equals(address)) {
            // just reconnect
            result = this.mBluetoothGatt.connect();
        }
        else {
            // connect from scratch
            // get BluetoothDevice object for specified address
            this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(this.mDeviceAddress);
            if (mBluetoothDevice == null) {
                // we got wrong address - that device is not available!
                result = false;
            }

            // connect with remote device
            this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(this.mParent, false, this.mBleCallback);

            try {
                if (this.mBluetoothGatt != null) {
                    double waitSeconds = 1;
                    int retryTimes = 5;
                    this.mBluetoothGatt.discoverServices();
                    while (this.mBluetoothGatt.getServices().size() == 0) {
                        if (retryTimes == 0) {
                            break;
                        }
                        this.mBluetoothGatt.discoverServices();
                        Thread.sleep((long) CommonHelper.SecsToMilliSeconds(waitSeconds));
                        retryTimes--;
                    }

                    if (this.mBluetoothGatt.getServices().size() > 0) {
                        Log.d(this.mTAG, "successfully connect to the target device");
                        this.mConnected = true;
                        result = true;
                    }
                    else {
                        Log.d(this.mTAG, "fail connect to the target device");
                    }
                }
            } catch (Exception ex) {
                Log.e(this.mTAG, ex.getMessage());
            }
        }

        return result;
    }

    /**
     * send data to remote device, data is bye stream
     * date: 2014/11/27
     *
     * @param serviceName     uuid of target service
     * @param characteristics uuid of target characteristics of service
     * @param data            data that we want to send
     * @author Yu-Hua Tseng
     */
    @Override
    public void writeData(UUID serviceName, UUID characteristics, byte[] data) {

        try {
            BluetoothGattService service = this.mBluetoothGatt.getService(serviceName);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristics);
            characteristic.setValue(data);
            this.mBluetoothGatt.writeCharacteristic(characteristic);
        } catch (Exception ex) {
            Log.e(this.mTAG, ex.getMessage());
        }
    }

    /**
     * to register notification
     * date: 2014/11/28
     *
     * @param serviceName     uuid of target service
     * @param characteristics uuid of target characteristics of service
     * @param enableFlag      enable notification mechanism or not
     * @author Yu-Hua Tseng
     */
    @Override
    public void setNotification(UUID serviceName, UUID characteristics, boolean enableFlag) {

        if (!this.mConnected || this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.e(this.mTAG, "cannot set Notification");
        }
        else {

            BluetoothGattService service = this.mBluetoothGatt.getService(serviceName);
            BluetoothGattCharacteristic ch = service.getCharacteristic(characteristics);
            boolean success = mBluetoothGatt.setCharacteristicNotification(ch, enableFlag);

            if (!success) {
                Log.e(this.mTAG, "orz! fail to set notifiation");
            }

            // This is also sometimes required (e.g. for heart rate monitors) to enable notifications/indications
            // see: https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorViewer.aspx?u=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
            BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            if (descriptor != null) {
                byte[] val = enableFlag ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(val);
                this.mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }


    /**
     * Get data from the specific characteristic
     * date: 2014/11/27
     *
     * @param serviceName     uuid of target service
     * @param characteristics uuid of target characteristics of service
     * @author Yu-Hua Tseng
     */
    public void getCharacteristicValue(UUID serviceName, UUID characteristics) {
        if (!this.mConnected || this.mBluetoothAdapter == null || this.mBluetoothGatt == null || characteristics == null) {
            Log.w(this.mTAG, "bluetoothadpater or gatt instance is null");
        }
        else {

            BluetoothGattService service = this.mBluetoothGatt.getService(serviceName);
            BluetoothGattCharacteristic ch = service.getCharacteristic(characteristics);

            byte[] rawValue = ch.getValue();
            String strValue = null;

            // lets read and do real parsing of some characteristic to get meaningful value from it
            UUID uuid = characteristics;

            // not known type of characteristic, so we need to handle this in "general" way
            // get first four bytes and transform it to integer
            int intValue = 0;
            if (rawValue.length > 0) intValue = (int) rawValue[0];
            if (rawValue.length > 1) intValue = intValue + ((int) rawValue[1] << 8);
            if (rawValue.length > 2) intValue = intValue + ((int) rawValue[2] << 8);
            if (rawValue.length > 3) intValue = intValue + ((int) rawValue[3] << 8);

            if (rawValue.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(rawValue.length);
                for (byte byteChar : rawValue) {
                    stringBuilder.append(String.format("%c", byteChar));
                }
                strValue = stringBuilder.toString();
            }

            String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
            this.mUICallback.uiNewValueForCharacteristic(strValue,
                    intValue, rawValue, timestamp);
        }
    }


    /**
     * start to monitor signal strength of target device that you connect to
     * date: 2014/11/27
     *
     * @author Yu-Hua Tseng
     */
    @Override
    public void startMonitoringRssiValue() {
        readPeriodicalyRssiValue(true);
    }

    /**
     * request new RSSi value for the connection
     * date: 2014/11/27
     *
     * @author Yu-Hua Tseng
     */
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
}
