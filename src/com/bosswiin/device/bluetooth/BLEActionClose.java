package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLEActionClose extends BLEActionBase {

    public BLEActionClose(){
    }

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
