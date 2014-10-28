package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import org.json.JSONObject;

/**
 * Created by 9708023 on 2014/10/28.
 */
public class BLEDeviceInfo {

    public String deviceName;

    public String deviceAddress;

    // the strength of radio
    public String rssi;

    public JSONObject uuidList=null;

    protected BluetoothDevice bluetoothDevice=null;
}
