package com.bosswiin.device.bluetooth;

import com.bosswiin.device.bluetooth.blehandelr.*;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLERequest {

    protected BLEAcionEnum actionEnum=BLEAcionEnum.None;

    private BleWrapper bleWrapper;

    public BLERequest() {

    }

    public synchronized void SetRequestType(BLEAcionEnum  action)
    {
        this.actionEnum=action;
    }

    protected synchronized void SetWrapper(BleWrapper wrapper)
    {
        this.bleWrapper=wrapper;
    }

    protected BleWrapper GetWrapper()
    {
        return this.bleWrapper;
    }
}
