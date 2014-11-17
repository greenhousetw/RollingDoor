/**
 * BLEActionSend.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import com.bosswiin.sharelibs.CommonHelper;

import java.util.UUID;

/**
 * BLEActionSend
 * This class will register notification signal which sent by peripheral
 */
public class BLEActionNotificaiton extends BLEActionBase {

    // tag string for logging
    private final String logTag = this.getClass().getName();

    /**
     * To execute the action.
     * date: 2014/10/31
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean executeAction(BLERequest request) {

        boolean result = false;

        try {

            Log.d(logTag, "send data:" + request.transmittedContent + " to Characteristic:" + request.characteristicsUUID + " of service uuid:" + request.serviceUUID);
            Thread.sleep((int) CommonHelper.SecsToMilliSeconds(0.3));
            for (BluetoothGattCharacteristic characteristic : request.targetService.getCharacteristics()) {
                if (characteristic.getUuid().equals(UUID.fromString(request.characteristicsUUID))) {
                    request.bleWrapper.setNotificationForCharacteristic(characteristic, true);
                    request.handler.registerNotification(characteristic);
                    Log.d(logTag, "Notification:" + UUID.fromString(request.characteristicsUUID) + " has been registered");
                    result = true;
                }
            }

        } catch (Exception ex) {
            Log.e(logTag, ex.getMessage());
        }

        return result;
    }

    /**
     * Check the job belongs to me
     * date: 2014//11/17
     *
     * @param response instance of BLEAction of successor
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean isMyDuty(BLEAcionEnum response) {

        boolean result = true;

        if (response != BLEAcionEnum.Notification) {
            result = false;
        }

        return result;
    }
}