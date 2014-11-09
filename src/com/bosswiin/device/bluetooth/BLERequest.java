/**
 * BLERequest.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;

/**
 * BLERequest
 * This class is a request of BLE, this class still encapsulates BLEWrapper, BLEActionEnum and BluetoothDevice
 */
public class BLERequest {

    // instance of BLEActionEnum
    public    BLEAcionEnum      actionEnum          = BLEAcionEnum.None;
    // address that we want to connect
    public    String            remoteAddress       = "";
    // uudi of service
    public    String            serviceUUID         = "";
    // uuid of characteristics
    public    String            characteristicsUUID = "";
    // content for receieving or transmitting
    public   byte[] transmittedContent  = null;
    // handler for notification
    public INotificationHandler handler=null;
    // instance of BLEWrapper
    protected BleWrapper        bleWrapper          = null;
    // instance of BluetoothDevice
    protected BluetoothDevice   bluetoothDevice     = null;
    // context in runtime, usually is Activity
    protected Context context;
    // target service
    protected BluetoothGattService targetService=null;
}
