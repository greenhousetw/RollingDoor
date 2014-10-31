/**
 * BLEActionDisconnect.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

/**
 * BLEActionDisconnect
 * This class will disconnect from remote peripheral
 */
public class BLEActionDisconnect extends BLEActionBase {

    public BLEActionDisconnect(){
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.Diconnect)
        {
            result=this.successor.Execute(request);
        }
        else
        {
            request.bleWrapper.diconnect();
        }

        return result;
    }
}
