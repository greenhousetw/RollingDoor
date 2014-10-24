package com.bosswiin.device.bluetooth;

import android.bluetooth.*;
import android.content.Context;
import com.bosswiin.sharelibs.*;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BossWiinBlueToothManager {

    private BLEActionBase openAction, closeAction, scanAction, checkBLE;

    private BluetoothManager bluetoothManager = null;

    private Context context;

    private  BluetoothAdapter bluetoothAdapter=null;

    public BossWiinBlueToothManager(){

        this.context=ContextHelper.GetGlobalContext();

        this.openAction=new BLEOpen();
        this.closeAction=new BLEClose();
        this.scanAction=new BLEScan();
        this.checkBLE=new BLECheckEquipment();

        this.openAction.SetSuccessor(this.scanAction);
        this.closeAction.SetSuccessor(this.closeAction);
        this.closeAction.SetSuccessor(this.checkBLE);

        this.bluetoothManager= (BluetoothManager) this.context.getSystemService(this.context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();

    }

    public boolean Execute(BLERequest request) {

        request.SetAdapter(this.bluetoothAdapter);
        return this.openAction.Execute(request);

    }

}
