package com.bosswiin.device.bluetooth;

import android.os.Binder;

/**
 * Created by 9708023 on 2014/12/2.
 */
public class CJBLEBinder extends Binder {

    private IJBTManagerUICallback callbackInstance=null;

    public CJBLEBinder(){

    }

    public CJBLEBinder(IJBTManagerUICallback uiCallback){
        this.callbackInstance=uiCallback;
    }



}
