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
public class BLECheckEquipment extends BLEActionBase {

    public BLECheckEquipment(){
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

            Context context = ContextHelper.GetGlobalContext();

            BleWrapper bleWrapper=request.GetWrapper();

            // check for Bluetooth enabled on each resume
            if (bleWrapper.isBtEnabled() == false)
            {
                // Bluetooth is not enabled. Request to user to turn it on
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // startActivity((Activity) context);
                // finish();
            }

            // init ble wrapper
            bleWrapper.initialize();
        }

        return result;
    }
}
