/**
 * ChwanJheDeviceManager.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.devicemanager;

import android.content.Context;
import android.util.Log;
import com.bosswiin.repository.IRepository;
import com.bosswiin.repository.RepositoryEnum;
import com.bosswiin.repository.RepositoryFactory;
import com.bosswiin.sharelibs.JSONHelper;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * ChwanJheDeviceManager
 * This class manages peripheral related database operations
 */
public class ChwanJheDeviceManager implements IJBTDeviceManager {

    // the name of main table
    private final String mTableName    = "DeviceList";
    // the name of database
    private final String mDatabaseName = "info.db";
    // the runtime context
    private Context     context;
    // the instance of repository
    private IRepository repository;
    // for log showing
    private String                  logTag     = ChwanJheDeviceManager.class.getName();
    // the instance of device collection
    private HashMap<String, Object> deviceInfo = new HashMap<String, Object>();

    /**
     * Initializes a new instance of the ChwanJheDeviceManager class.
     * date: 2014/11/18
     *
     * @param context to use to open or create the database
     * @author Yu-Hua Tseng
     */
    public ChwanJheDeviceManager(Context context, boolean enableSimulation) {

        if (context == null) {
            throw new IllegalArgumentException("context should not be null");
        }

        this.context = context;

        if (!this.getRepository()) {
            throw new IllegalArgumentException("orz!" + this.mDatabaseName + " init fail.");
        }

        if (enableSimulation) {
            this.simulateData();
        }
    }

    /**
     * Set data into data collection and save to database
     * date: 2014/11/18
     *
     * @param address  address of peripheral
     * @param name     name of address
     * @param location location of address
     * @param freq     use frequency
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean saveSingleDevice(String address, String name, String location, String freq) {

        boolean result = false;

        try {
            this.deviceInfo.clear();
            this.deviceInfo.put("address", address);
            this.deviceInfo.put("name", name);
            this.deviceInfo.put("location", location);
            this.deviceInfo.put("frequency", freq);
            this.deviceInfo.put("updateTime", new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
            result = this.saveDevice("DeviceList", this.deviceInfo);
        } catch (Exception ex) {
            Log.e(this.logTag, ex.getMessage());
        }

        return result;
    }

    /**
     * Insert device into database
     * date: 2014/11/18
     *
     * @param tableName     the name of table
     * @param databaseTuple is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public boolean saveDevice(String tableName, HashMap<String, Object> databaseTuple) {
        boolean result = false;

        if (tableName.length() != 0 && this.deviceInfo != null) {
            try {
                this.repository.Insert(tableName, databaseTuple);
                result = true;
            } catch (Exception ex) {
                Log.e(this.logTag, ex.getMessage());
            }
        }

        return result;
    }

    /**
     * get all devices from database
     * date: 2014/11/18
     *
     * @param tableName     the name of table
     * @param databaseTuple is a HashMap, key means column's name, value means the content of column
     * @return return value will be encapsulate into ArrayList
     * @author Yu-Hua Tseng
     */
    @Override
    public JSONArray getDeviceList(String tableName, HashMap<String, Object> databaseTuple) {

        JSONArray result = null;

        if (tableName.length() != 0 && databaseTuple != null) {
            try {
                databaseTuple.clear();
                result = JSONHelper.GetJSON(this.repository.Query(tableName, databaseTuple));
            } catch (Exception ex) {
                Log.e(this.logTag, ex.getMessage());
            }
        }

        return result;
    }

    /**
     * Pass data to activity for ui related operations
     * date: 2014/11/09
     *
     * @return true for successful and false for fail
     * @author Yu-Hua Tseng
     */
    private boolean getRepository() {

        boolean result = false;

        if (this.repository == null) {

            int dbVersion = 1;
            final String dbInitString = "CREATE TABLE IF NOT EXISTS DeviceList(" +
                    "address    VARCHAR( 60 ) PRIMARY KEY," +
                    "name       VARCHAR( 100 )," +
                    "location   VARCHAR( 50 )," +
                    "frequency  INT," +
                    "updateTime DATETIME" +
                    ");";

            this.repository = RepositoryFactory.GetRepository(this.context, this.mDatabaseName, dbVersion, RepositoryEnum.SQLite3, dbInitString);

            if (this.repository != null) {
                result = true;
            }
        }

        return result;
    }

    /**
     * remove all peripherals from database
     * date: 2014/11/18
     *
     * @author Yu-Hua Tseng
     */
    private void clearRecords(boolean isCleanAll) {

        if (isCleanAll) {
            this.deviceInfo.clear();
            this.deviceInfo.put("Where", "");
            this.repository.Delete(this.mTableName, this.deviceInfo);
        }

    }

    /**
     * this method only for simulation
     * date: 2014/11/18
     *
     * @author Yu-Hua Tseng
     */
    private void simulateData() {

        String testData[][] = {
                {"E6:F0:28:07:56:19", "ChwanJhe", "B1", "1"}
        };

        this.clearRecords(true);

        for (int i = 0; i < testData.length; i++) {
            this.saveSingleDevice(testData[i][0], testData[i][1], testData[i][2], testData[i][3]);
        }
    }
}
