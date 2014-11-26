package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by 9708023 on 2014/11/26.
 */
public interface IBLEHandler {

    void scanDevice();
    boolean initialize();
    boolean connect(String address);
    void writeData(UUID serviceName, UUID characteristics, byte[] data);
}
