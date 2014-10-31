/**
 * BLEActionScan.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * BLEActionScan
 * This class scan all BLE peripherals around the device
 */
public class BLEActionScan extends BLEActionBase {

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

        if(request.actionEnum != BLEAcionEnum.Scan)
        {
           result=this.successor.Execute(request);
        }
        else
        {
            try {
                Log.v(this.getClass().getPackage().getName(), "Start to scan BLE device");
                request.bleWrapper.startScanning();
                result = true;
            }
            catch (Exception ex)
            {
                Log.e(this.getClass().getName(), ex.getMessage());
            }
        }

        return result;
    }
}
