package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by YuHua on 2014/11/8.
 */
public interface IDeviceFoundCallback {

    void addNewDevices(String deviceName, String address, final int rssi, final byte[] record);
}
