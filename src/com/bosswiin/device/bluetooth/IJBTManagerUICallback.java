/**
 * public interface IJBTManagerUICallback.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

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
     * Callback method for disconnection
     * date: 2014/11/09
     *
     * @param address the address of remote device
     * @author Yu-Hua Tseng
     */
    void uiDeviceDisconnected(String address);

    /**
     * Callback method for write data to remote device
     * date: 2014/11/09
     *
     * @param address the address of remote device
     * @param chName uuid value of the target characteristic
     * @param description characteristic's description
     * @param operationresult result of this operation
     * @author Yu-Hua Tseng
     */
    void uiWriteResult(String address, String chName, String description, boolean operationresult);

    /**
     * Callback method for write data to remote device
     * date: 2014/11/09
     *
     * @param address the address of remote device
     * @param rssi the value of current ssi
     * @author Yu-Hua Tseng
     */
    void uiNewRssiAvailable(String address, final int rssi);

    /**
     * callback function for notification of new data ready in remote characteristic
     * date: 2014/11/09
     *
     * @param strValue the value of characteristic
     * @param intValue the value of characteristic
     * @param rawValue the value of characteristic
     * @param timestamp time stamp of this data
     * @author Yu-Hua Tseng
     */
    public void uiNewValueForCharacteristic(final String strValue,
                                            final int intValue,
                                            final byte[] rawValue,
                                            final String timestamp);
}
