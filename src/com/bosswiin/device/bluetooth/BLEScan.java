package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLEScan extends BLEActionBase {

    public BLEScan(){
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
            Log.v(this.getClass().getPackage().getName(), "Start to scan BLE device");
            request.GetWrapper().startScanning();
        }

        return result;
    }
}
