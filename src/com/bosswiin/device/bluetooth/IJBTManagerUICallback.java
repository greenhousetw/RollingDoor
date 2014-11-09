/**
 * public interface IJBTManagerUICallback.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

/**
 * IDeviceFoundCallback
 * To implement this interface, then JBluetothManager can pass discovered peripheral into the related user interface
 */
public interface IJBTManagerUICallback {

    /**
     * This method should notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param deviceName name of peripheral
     * @param address    address of peripheral
     * @param rssi       signal strength of peripheral
     * @param record     other record of peripheral
     * @author Yu-Hua Tseng
     */
    void addNewDevices(String deviceName, String address, final int rssi, final byte[] record);

    /**
     * Pass data to activity for ui related operations
     * date: 2014/11/09
     *
     * @param data data that needs to be process
     * @author Yu-Hua Tseng
     */
    void passContentToActivity(Object data);
}
