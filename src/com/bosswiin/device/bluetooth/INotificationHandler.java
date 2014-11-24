/**
 * public interface INotificationHandler.java
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
 * INotificationHandler
 * This is callback interface for Bluetooth low energy notification mechanism
 */
public interface INotificationHandler {

    /**
     * To register notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param bluetoothGattCharacteristic instance of BluetoothGattCharacteristic
     * @author Yu-Hua Tseng
     */
    public void registerNotification(BluetoothGattCharacteristic bluetoothGattCharacteristic);

    /**
     * This method should notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param gatt instance of BluetoothGatt
     * @param device instance of BluetoothDevice
     * @param service instance of BluetoothGattService
     * @param ch instance of BluetoothGattCharacteristic
     * @param strValue the data is stored in remote peripheral, type in string
     * @param intValue the data is stored in remote peripheral, type in int
     * @param rawValue the data is stored in remote peripheral, type in byte
     * @param timestamp time stamp for receiving
     * @author Yu-Hua Tseng
     */
    public void handleNotification(BluetoothGatt gatt,
                                   BluetoothDevice device, BluetoothGattService service,
                                   BluetoothGattCharacteristic ch, String strValue, int intValue,
                                   byte[] rawValue, String timestamp);


    /**
     * process new Rssi value
     * date: 2014/11/14
     *
     * @param gatt instance of BluetoothGatt
     * @param device instance of BluetoothDevice
     * @param rssi strength of signal
     * @author Yu-Hua Tseng
     */
    public void handleNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi);


    /**
     * process new Rssi value
     * date: 2014/11/24
     *
     * @param gatt instance of BluetoothGatt
     * @param device instance of BluetoothDevice
     * @author Yu-Hua Tseng
     */
    public void handleDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device);
}
