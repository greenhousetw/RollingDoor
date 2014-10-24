package com.bosswiin.device.bluetooth;

import android.bluetooth.*;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLEActionBase implements IHandler {

    protected  IHandler successor=null;

    public BLEActionBase(){
    }


    public void SetSuccessor(IHandler handler){
        this.successor=handler;
    }

    @Override
    public boolean Execute(BLERequest request) {
        return false;
    }
}
