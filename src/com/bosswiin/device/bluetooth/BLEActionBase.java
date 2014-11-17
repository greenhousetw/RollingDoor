/**
 * BLEActionBase.java
 * @author Yu-Hua Tseng
 * @version 0.2
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

/**
 * BLEActionBase
 * This class is base class for all BLEAction, main focus to set its successor for responsibility chain
 */
public class BLEActionBase implements IHandler {

    // execution handler
    protected  IHandler successor=null;

    /**
     * Check the job belongs to me
     * date: 2014//11/17
     *
     * @param response instance of BLEAction of successor
     * @author Yu-Hua Tseng
     */
    public boolean isMyDuty(BLEAcionEnum response){
        return false;
    }

    /**
     * Insert BluetoothDevice into collection for future using
     * date: 2014/10/24
     *
     * @param handler instance of BLEAction of successor
     * @author Yu-Hua Tseng
     */
    public void setSuccessor(IHandler handler){
        this.successor=handler;
    }

    /**
     * To execute BLE request
     * date: 2014/10/24
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail.
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean execute(BLERequest request) {
        boolean result = false;

        if (!this.isMyDuty(request.actionEnum)) {
            result = this.successor.execute(request);
        } else {
           result=this.executeAction(request);
        }

        return  result;
    }


    /**
     * To execute the action.
     * date: 2014/10/31
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    public boolean executeAction(BLERequest request) {
        return false;
    }
}
