/**
 * BLEActionChungJeSend.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.UUID;

/**
 * Created by 9708023 on 2014/11/17.
 */
public class BLEActionChungJeSend extends BLEActionSend {

    /**
     * To Send data to the given characteristic.
     * date: 2014//11/17
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean sendData(BLERequest request) {

        boolean result = false;

        try {
            Log.d(BLEActionSend.class.getName(), "Send data in characteristic id=" + request.characteristicsUUID);
            BluetoothGattCharacteristic bleWriter=request.targetService.getCharacteristic(UUID.fromString(request.characteristicsUUID));
            request.bleWrapper.writeDataToCharacteristic(bleWriter, request.transmittedContent);
        }
        catch (Exception ex) {
            Log.e(BLEActionChungJeSend.class.getName(), ex.getMessage());
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

        boolean result=true;

        if(response != BLEAcionEnum.ChungJeSend){
            result=false;
        }

        return result;
    }
}
