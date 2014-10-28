package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/27.
 */
public class BLEActionSend extends BLEActionBase {

    public BLEActionSend(){
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.Send)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), "Start to scan BLE device");
        }

        return result;
    }
}

