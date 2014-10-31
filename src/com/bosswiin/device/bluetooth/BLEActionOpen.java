/**
 * BLEActionOpen.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * BLEOpen
 * This class will execute connection action between android device and BLE peripheral
 */
public class BLEActionOpen extends BLEActionBase {

    @Override
    public boolean Execute(BLERequest request) {

        boolean result = false;

        if (request.actionEnum != BLEAcionEnum.Open) {
            result = this.successor.Execute(request);
        } else {
            Log.d(this.getClass().getPackage().getName(), "Start to open connection");
            try {
                result = request.bleWrapper.connect(request.remoteAddress);
                Log.d(this.getClass().getPackage().getName(), "Connect to Bluetooth Device:" + request.remoteAddress + ", status=" + Boolean.toString(result));
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), ex.getMessage());
            }
        }

        return result;
    }
}
