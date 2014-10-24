package com.bosswiin.device.bluetooth;

import android.app.DownloadManager;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BlueToothManager {

    private BLEActionBase openAction, closeAction, scanAction;

    public BlueToothManager(){

        this.openAction=new BLEOpen();
        this.closeAction=new BLEClose();
        this.scanAction=new BLEScan();

        this.openAction.SetSuccessor(this.scanAction);
        this.closeAction.SetSuccessor(this.closeAction);
    }

    public boolean Execute(BLERequest request) {

        return this.openAction.Execute(request);

    }

}
