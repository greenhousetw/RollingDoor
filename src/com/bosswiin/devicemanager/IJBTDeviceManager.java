package com.bosswiin.devicemanager;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by 9708023 on 2014/11/18.
 */
public interface IJBTDeviceManager {

    /**
     * Insert device into database
     * date: 2014/11/18
     *
     * @param tableName     the name of table
     * @param databaseTuple is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    boolean saveDevice(String tableName, HashMap<String, Object> databaseTuple);

    /**
     * get all devices from database
     * date: 2014/11/18
     *
     * @param tableName     the name of table
     * @param databaseTuple is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    JSONArray getDeviceList(String tableName, HashMap<String, Object> databaseTuple);

    /**
     * Set data into data collection and save to database
     * date: 2014/11/10
     *
     * @param address  address of peripheral
     * @param name     name of address
     * @param location location of address
     * @param freq     use frequency
     * @author Yu-Hua Tseng
     */
    boolean saveSingleDevice(String address, String name, String location, String freq);
}
