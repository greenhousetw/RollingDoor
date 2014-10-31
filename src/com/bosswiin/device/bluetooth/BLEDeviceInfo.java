/**
 * BLEDeviceInfo.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import org.json.JSONObject;

/**
 * The class describes the information of one BLE device
 */
public class BLEDeviceInfo {

    // the name of device
    public String deviceName;

    // the address of device
    public String deviceAddress;

    // the strength of radio
    public String rssi;

    // uuid list for device's sub services
    public JSONObject uuidList = null;
}