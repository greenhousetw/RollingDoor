/**
 * BLERequest.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import com.bosswiin.device.bluetooth.blehandelr.*;

/**
 * BLERequest
 * This class is a request of BLE, this class still encapsulates BLEWrapper, BLEActionEnum and BluetoothDevice
 */
public class BLERequest {

    // instance of BLEActionEnum
    protected BLEAcionEnum actionEnum = BLEAcionEnum.None;

    // instance of BLEWrapper
    protected BleWrapper bleWrapper = null;

    // instance of BluetoothDevice
    protected BluetoothDevice bluetoothDevice = null;
}
