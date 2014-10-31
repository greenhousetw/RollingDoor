/**
 * BLEActionCheckEquipment.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.content.*;
import android.util.Log;
import android.bluetooth.*;

/**
 * BLEActionCheckEquipment
 * This class will ask user to turn on Bluetooth which its staus is off
 */
public class BLEActionCheckEquipment extends BLEActionBase {

    /**
     * To execute BLE request
     * date: 2014/10/24
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail.
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.CheckEquipment)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), this.getClass().getName());

            // check for Bluetooth enabled on each resume
            if (request.bleWrapper.isBtEnabled() == false)
            {
                // Bluetooth is not enabled. Request to user to turn it on
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // startActivity((Activity) context);
                // finish();
            }

            // init ble wrapper
            request.bleWrapper.initialize();
        }

        return result;
    }
}
