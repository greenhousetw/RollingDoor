package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLEActionScan extends BLEActionBase {

    public BLEActionScan(){
    }

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
