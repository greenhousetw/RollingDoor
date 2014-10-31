/**
 * BLEActionBase.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.device.bluetooth;

/**
 * IHandler
 * This interface is a standard for BLE request execution
 */
public interface IHandler {

    /**
     * To execute BLE request
     * date: 2014/10/24
     *
     * @param request instance of BLERequest
     * @return true for successful and false for fail.
     * @author Yu-Hua Tseng
     */
    boolean Execute(BLERequest request);

}
