/**
 * BLEAcionEnum.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

/**
 * BLE action enumeration definition
 * This class is enumeration set only
 */
public enum BLEAcionEnum {

    // to enable bluetooth device
    Open,

    // to close bluetooth device
    Close,

    // to scan all BLE devices around us
    Scan,

    // to stop scanning
    StopScan,

    // to send data to remote device that we connect to
    Send,

    // to check device is BLE capable
    CheckEquipment,

    // to disconnect BLE connection
    Diconnect,

    // to get data from notification
    Notification,

    // no meaning
    None
}
