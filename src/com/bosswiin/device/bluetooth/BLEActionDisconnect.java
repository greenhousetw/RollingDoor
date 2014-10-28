package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/27.
 */
public class BLEActionDisconnect extends BLEActionBase {

    public BLEActionDisconnect(){
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.Diconnect)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            request.bleWrapper.diconnect();
        }

        return result;
    }
}
