package com.bosswiin.device.bluetooth;

import android.app.*;
import android.content.*;
import android.util.Log;
import android.bluetooth.*;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;
import com.bosswiin.sharelibs.*;

/**
 * Created by 9708023 on 2014/10/24.
 */
public class BLEActionCheckEquipment extends BLEActionBase {

    public BLEActionCheckEquipment(){
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.CheckEquipment)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), this.getClass().getName());

            // check for Bluetooth enabled on each resume
            if (request.bleWrapper.isBtEnabled() == false)
            {
                // Bluetooth is not enabled. Request to user to turn it on
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // startActivity((Activity) context);
                // finish();
            }

            // init ble wrapper
            request.bleWrapper.initialize();
        }

        return result;
    }
}
