package com.bosswiin.device.bluetooth;

import android.content.*;
import android.util.Log;
import android.content.pm.PackageManager;
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

        if(request.actionEnum != BLEAcionEnum.CheckLE)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), this.getClass().getName());

            Context context = ContextHelper.GetGlobalContext();

            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            {
                result=true;
            }
        }

        return result;
    }
}
