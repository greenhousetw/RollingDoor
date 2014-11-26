package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * Created by 9708023 on 2014/11/26.
 */
public interface IBLEHandler {

    void scanDevice();

    boolean connect();

    boolean getGattService(BluetoothGattService gattService);

    void writeCharacteristic(BluetoothGattCharacteristic characteristic);

    void setRegisterReceiver();

    boolean stopregisterReceiver();

    void startReadRssi();
}
