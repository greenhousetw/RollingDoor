/**
 * BLEActionClose.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * BLEActionClose
 * This class closes the GATT of selected BLE peripheral
 */
public class BLEActionClose extends BLEActionBase {

    public BLEActionClose(){
    }

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

        if(request.actionEnum != BLEAcionEnum.Close)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), "Close BLE connection");
            request.bleWrapper.close();
        }

        return result;
    }
}
