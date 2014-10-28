package com.bosswiin.device.bluetooth;

import android.bluetooth.*;
import android.util.Log;
import com.bosswiin.device.bluetooth.blehandelr.BleWrapper;

import java.util.*;

/**
 * Created by 9708023 on 2014/10/28.
 */
public class BLEActionGetServices extends BLEActionBase {

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
           this.GetAvailableServices(request.bleWrapper, request.bleWrapper.getGatt(), request.bleWrapper.getDevice(),
                   request.bleWrapper.getCachedServices());
        }

        return result;
    }

    public void GetAvailableServices(final BleWrapper bleWrapper,
                                     final BluetoothGatt gatt,
                                     final BluetoothDevice device,
                                     final List<BluetoothGattService> services)
    {
      for(BluetoothGattService service : bleWrapper.getCachedServices()) {

      }
    }
}
