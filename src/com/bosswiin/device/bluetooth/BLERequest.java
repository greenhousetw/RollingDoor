package com.bosswiin.device.bluetooth;

import android.bluetooth.*;
import java.util.*;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLERequest {

    protected BLEAcionEnum actionEnum=BLEAcionEnum.None;

    private HashMap<BLEAcionEnum, Object> dataSet=new HashMap<BLEAcionEnum, Object>();

    private BluetoothAdapter bluetoothAdapter;

    public BLERequest() {

    }

    public synchronized  void SetRequestType(BLEAcionEnum  action){
        this.actionEnum=action;
    }

    protected synchronized void SetAdapter(BluetoothAdapter adapter)
    {
        this.bluetoothAdapter=adapter;
    }

    public synchronized boolean PushData(BLEAcionEnum keyName, Object value){

        boolean result=false;

        if(keyName != BLEAcionEnum.None) {
            this.dataSet.remove(keyName);
            this.dataSet.put(keyName, value);
            result=true;
        }

        return  result;
    }

    public Object GetData(BLEAcionEnum keyName)
    {
        Object returnValue=null;

        if(this.dataSet.containsKey(keyName))
        {
            returnValue=this.dataSet.get(keyName);
        }
        return returnValue;
    }
}
