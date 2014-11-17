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
 * This class sends data to remote BLE device
 */
public class BLEActionSend extends BLEActionBase {

    // tag string for loggin
    private final String logTag = this.getClass().getName();

    /**
     * To execute the action.
     * date: 2014/10/31
     * the history:
     * reduce waitting to 0.08 and add retry times to 5
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean executeAction(BLERequest request) {

        boolean result = false;

        try {

            double waitTime = 0.08;
            int retryTimes = 5;

            while (!result) {

                if(retryTimes == 0)
                {
                    break;
                }

                Log.d(logTag, "send data:" + request.transmittedContent + " to Characteristic:" + request.characteristicsUUID + " of service uuid:" + request.serviceUUID);
                Thread.sleep((int) CommonHelper.SecsToMilliSeconds(waitTime));

                if (this.sendData(request)) {
                    result = true;
                    break;
                }

                retryTimes--;
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

        if (response != BLEAcionEnum.Send) {
            result = false;
        }

        return result;
    }

    /**
     * To Send data to the given characteristic.
     * date: 2014//11/17
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean sendData(BLERequest request) {

        boolean result = false;

        try {

            Log.d(BLEActionSend.class.getName(), "Send data");
            for (BluetoothGattCharacteristic characteristic : request.targetService.getCharacteristics()) {
                if (characteristic.getUuid().equals(UUID.fromString(request.characteristicsUUID))) {
                    request.bleWrapper.writeDataToCharacteristic(characteristic, request.transmittedContent);
                    result = true;
                    break;
                }
            }
        } catch (Exception ex) {
            Log.e(logTag, ex.getMessage());
        }

        return result;
    }
}