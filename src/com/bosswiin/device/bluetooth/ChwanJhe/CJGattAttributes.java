package com.bosswiin.device.bluetooth.ChwanJhe;

import java.util.HashMap;

/**
 * Created by 9708023 on 2014/11/26.
 */
public class CJGattAttributes {

    private static HashMap<String, String> attributes = new HashMap<String, String>();

    public static String CJWDoorService = "713d0000-503e-4c75-ba94-3148f18d941e";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BLE_Sending_Charactristics = "713d0003-503e-4c75-ba94-3148f18d941e";
    public static String BLE_Receive_Charactristics = "713d0002-503e-4c75-ba94-3148f18d941e";
    public static String BLE_SHIELD_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";
    public static String ServiceName="ChwanJheService";

    static {
        attributes.put(CJWDoorService, ServiceName);
        attributes.put(BLE_Sending_Charactristics, "BLE Shield TX");
        attributes.put(BLE_Receive_Charactristics, "BLE Shield RX");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
