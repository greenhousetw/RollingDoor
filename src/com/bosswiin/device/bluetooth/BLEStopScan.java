package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/27.
 */
public class BLEStopScan extends BLEActionBase {

    public BLEStopScan(){
    }

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

            request.GetWrapper().startScanning();
        }

        return result;
    }
}

