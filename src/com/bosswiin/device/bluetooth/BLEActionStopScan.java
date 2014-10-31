/**
 * BLEActionStopScan.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * BLEActionStopScan
 * This class stops BLE scanning
 */
public class BLEActionStopScan extends BLEActionBase {

    /**
     * To execute the action.
     * date: 2014/10/31
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.StopScan)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), this.getClass().getName());
            request.bleWrapper.stopScanning();
            result=true;
        }

        return result;
    }
}

