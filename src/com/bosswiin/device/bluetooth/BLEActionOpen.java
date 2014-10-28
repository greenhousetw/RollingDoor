/**
 * BLEOpen.java
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

        boolean result=false;

        Log.d(this.getClass().getPackage().getName(), "Start to open connection");

        if(request.actionEnum != BLEAcionEnum.Open)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            String address=request.bluetoothDevice.getAddress();
            result=request.bleWrapper.connect(address);
            Log.d(this.getClass().getPackage().getName(), "Connect to Bluetooth Device:" + address + ", status=" + Boolean.toString(result));
        }

        return result;
    }
}
