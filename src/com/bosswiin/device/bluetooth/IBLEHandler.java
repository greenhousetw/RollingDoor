package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Created by 9708023 on 2014/11/26.
 */
public interface IBLEHandler {

    /**
     * Initialize BLE and get BT Manager & Adapter
     * date: 2014/11/27
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    boolean initializeBTAdapter();

    /**
     * connect to the target device
     * date: 2014/11/27
     *
     * @param address the address of remote device
     * @return true for successfully check service count is more than 0 and false for fail
     * @author Yu-Hua Tseng
     */
    boolean connect(String address);

    /**
     * connect to the target device
     * date: 2014/11/27
     *
     * @param address        the address of remote device
     * @param service        the uuid of service
     * @param characteristic the uuid of characteristic
     * @return true for successfully check service count is more than 0 and false for fail
     * @author Yu-Hua Tseng
     */
    BluetoothGattCharacteristic connect(String address, UUID service, UUID characteristic);

    /**
     * Get data from the specific characteristic
     * date: 2014/11/27
     *
     * @param serviceName uuid of target service
     * @param characteristics  uuid of target characteristics of service
     * @author Yu-Hua Tseng
     */
    void getCharacteristicValue(UUID serviceName, UUID characteristics);

    /**
     * Get data from the specific characteristic
     * date: 2014/11/27
     * @param ch  BluetoothGattCharacteristic instance of target characteristics of service
     * @author Yu-Hua Tseng
     */
    void getCharacteristicValue(BluetoothGattCharacteristic ch);

    /**
     * send data to remote device, data is bye stream
     * date: 2014/11/27
     *
     * @param serviceName uuid of target service
     * @param characteristics  uuid of target characteristics of service
     * @param data data that we want to send
     * @author Yu-Hua Tseng
     */
    void writeData(UUID serviceName, UUID characteristics, byte[] data);

    /**
     * send data to remote device, data is bye stream
     * date: 2014/11/27
     *
     * @param characteristic  BluetoothGattCharacteristic instance of target characteristics of service
     * @param data data that we want to send
     * @author Yu-Hua Tseng
     */
    void writeData(BluetoothGattCharacteristic characteristic, byte[] data);

    /**
     * to register notification
     * date: 2014/11/28
     *
     * @param serviceName     uuid of target service
     * @param characteristics uuid of target characteristics of service
     * @param enableFlag enable notification mechanism or not
     * @author Yu-Hua Tseng
     */
    void setNotification(UUID serviceName, UUID characteristics, boolean enableFlag);

    /**
     * start to monitor signal strength of target device that you connect to
     * date: 2014/11/27
     *
     * @author Yu-Hua Tseng
     */
    void startMonitoringRssiValue();

    /**
     * to close current connected characteristic
     * date: 2014/11/27
     *
     * @author Yu-Hua Tseng
     */
    void closeConnection();
}
