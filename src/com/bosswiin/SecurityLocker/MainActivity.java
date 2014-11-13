package com.bosswiin.SecurityLocker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.bosswiin.UserInterface.Components.BLEAdpaterBase;
import com.bosswiin.UserInterface.Components.BLESimpleAdapter;
import com.bosswiin.device.bluetooth.BLEAcionEnum;
import com.bosswiin.device.bluetooth.BLERequest;
import com.bosswiin.device.bluetooth.IJBTManagerUICallback;
import com.bosswiin.device.bluetooth.JBluetoothManager;
import com.bosswiin.repository.IRepository;
import com.bosswiin.repository.RepositoryEnum;
import com.bosswiin.repository.RepositoryFactory;
import com.bosswiin.sharelibs.JSONHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener, IJBTManagerUICallback {

    private final String   uuidDoorService               = "713d0000-503e-4c75-ba94-3148f18d941e";
    private final String   uuidDoorCharactristicsForRead = "713d0002-503e-4c75-ba94-3148f18d941e";
    private final String   uuidDoorCharactristicsForUP   = "713d0003-503e-4c75-ba94-3148f18d941e";
    private final String   uuidDoorCharactristicsForDown = "713d0003-503e-4c75-ba94-3148f18d941e";
    private final String   uuidDoorCharactristicsForStop = "713d0003-503e-4c75-ba94-3148f18d941e";
    private final String   logTag                        = MainActivity.class.getName();
    private       ListView listView                      = null;
    private       Button   scanButton                    = null, upButton = null, stopButton = null, downButton = null;
    private IRepository             repository        = null;
    private String                  selectedAddress   = "";
    private String                  tableName         = "DeviceList";
    private Context                 mainContext       = this;
    private HashMap<String, Object> databaseTuple     = new HashMap<String, Object>();
    private JBluetoothManager       mJBluetootManager = null;
    private BLEAdpaterBase          bleAdpater        = null;
    private BLERequest              request           = new BLERequest();
    private MainActivity            activity          = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(this.logTag, "Application in onCreate phase");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        this.mJBluetootManager = new JBluetoothManager(this);
        //this.mJBluetootManager.setBluetoothLowEnergyWrapper(this);

        this.bleAdpater = new BLESimpleAdapter(this);
        this.listView = (ListView) this.findViewById(R.id.listView);
        this.listView.setEmptyView(findViewById(R.id.empty));
        this.scanButton = (Button) this.findViewById(R.id.buttonScan);
        this.upButton = (Button) this.findViewById(R.id.buttonUP);
        this.downButton = (Button) this.findViewById(R.id.buttonDown);
        this.stopButton = (Button) this.findViewById(R.id.buttonStop);

        this.scanButton.setOnClickListener(this);
        this.upButton.setOnClickListener(this);
        this.stopButton.setOnClickListener(this);
        this.downButton.setOnClickListener(this);

        this.listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                view.setSelected(true);
                TextView peripheralName = (TextView) view.findViewById(R.id.bleDeviceName);
                selectedAddress = peripheralName.getTag().toString();

                request.remoteAddress = selectedAddress;
                request.characteristicsUUID = uuidDoorCharactristicsForRead;
                request.serviceUUID = uuidDoorService;
                request.actionEnum = BLEAcionEnum.Notification;
                mJBluetootManager.changeBleDevice(request);
            }
        });

        listView.setAdapter(this.bleAdpater);
        //this.scanButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(this.logTag, "Application in start phase");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.logTag, "Application in resume phase");

        this.mJBluetootManager.enableBluetoothHardware(this);
        this.mJBluetootManager.setBluetoothLowEnergyWrapper(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.logTag, "Application in pause phase");

        this.mJBluetootManager.stopScanning();
        this.mJBluetootManager.stopMonitoringRSSI();
        this.mJBluetootManager.disconnect();
        this.mJBluetootManager.closeConnection();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(this.logTag, "Application in stop phase");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(this.logTag, "Application in destroy phase");
    }

    @Override
    public void onClick(android.view.View view) {

        try {

            String chatService = "up";
            this.request.actionEnum = BLEAcionEnum.Send;
            this.request.serviceUUID = this.uuidDoorService;
            this.request.remoteAddress = selectedAddress;

            if (view.getId() == R.id.buttonScan) {

                //this.mJBluetootManager.stopScanning();
                //this.mJBluetootManager.startScanning();
            }
            else if (this.request.remoteAddress.length() != 0) {

                if (view.getId() == R.id.buttonUP) {

                    this.request.characteristicsUUID = this.uuidDoorCharactristicsForUP;
                    //this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00};
                    this.request.transmittedContent = chatService.getBytes();
                    this.mJBluetootManager.executeRequest(this.request);

                }
                else if (view.getId() == R.id.buttonDown) {

                    this.request.characteristicsUUID = this.uuidDoorCharactristicsForDown;
                    chatService = "down";
                    this.request.transmittedContent = chatService.getBytes();
                    //this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00};
                    this.mJBluetootManager.executeRequest(this.request);
                }
                else if (view.getId() == R.id.buttonStop) {

                    this.request.characteristicsUUID = this.uuidDoorCharactristicsForStop;
                    chatService = "stop";
                    this.request.transmittedContent = chatService.getBytes();
                    //this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00};
                    this.mJBluetootManager.executeRequest(this.request);
                }
            }

        } catch (Exception ex) {
            Log.e(MainActivity.class.getName(), ex.getMessage());
        }
    }

    /**
     * This method should notification of bluetoothGattCharacteristic, you should call
     * date: 2014/11/09
     *
     * @param deviceName name of peripheral
     * @param address    address of peripheral
     * @param rssi       signal strength of peripheral
     * @param record     other record of peripheral
     * @author Yu-Hua Tseng
     */
    @Override
    public void addNewDevices(final String deviceName, final String address, final int rssi, final byte[] record) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleAdpater.AddNewDevice(deviceName, address);
                bleAdpater.notifyDataSetChanged();
            }
        });
    }

    /**
     * Pass data to activity for ui related operations
     * date: 2014/11/09
     *
     * @param data data that needs to be process
     * @author Yu-Hua Tseng
     */
    @Override
    public void passContentToActivity(Object data) {
        if (data != null) {
            ((EditText) this.findViewById(R.id.editnoticontent)).setText(data.toString().trim());
            ((EditText) this.findViewById(R.id.editnoticontent)).clearFocus();
            this.listView.requestFocus();
        }
    }

    /**
     * This is trail method
     * date: 2014/11/10
     *
     * @author Yu-Hua Tseng
     */
    private void InitDoorList() {

        String testData[][]={
            {"01:02:03:04:05:06","A","1F","1"},
            {"0a:0b:0c:0d:0e:0f","B","B1","1"}
        };

        for(int i=0; i< testData.length;i++){
            this.addDataToTable(testData[i][0],testData[i][1],testData[i][2],testData[i][3]);
        }

        // set column name
        this.databaseTuple.put("Address", "Address");
        this.databaseTuple.put("Name", "Name");
        this.databaseTuple.put("Location", "Location");
        this.databaseTuple.put("Frequency", "Frequency");
        this.databaseTuple.put("UpdateTime", "UpdateTime");

        JSONArray array = JSONHelper.GetJSON(this.repository.Query(this.tableName, this.databaseTuple));

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject record = array.getJSONObject(i);
                this.addNewDevices(record.get("Name").toString(),
                        record.get("Address").toString(),
                        0,
                        null);
            } catch (JSONException jsonEx) {
                Log.e(this.logTag, jsonEx.getMessage());
            }

        }
    }

    /**
     * Set data into data collection and save to database
     * date: 2014/11/10
     *
     * @param address address of peripheral
     * @param name    name of address
     * @param location location of address
     * @param freq use frequency
     * @author Yu-Hua Tseng
     */
    private void addDataToTable(String address, String name, String location, String freq) {

        try {
            this.databaseTuple.clear();
            this.databaseTuple.put("Address", address);
            this.databaseTuple.put("Name", name);
            this.databaseTuple.put("Location", location);
            this.databaseTuple.put("Frequency", freq);
            this.databaseTuple.put("UpdateTime", new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
            this.repository.Insert("DeviceList", this.databaseTuple);
        }
        catch (Exception ex)
        {
            Log.e(this.logTag, ex.getMessage());
        }
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

            String dbName = "info.db";
            int dbVersion = 1;
            final String dbInitString = "CREATE TABLE IF NOT EXISTS DeviceList(" +
                    "Address    VARCHAR( 60 )   PRIMARY KEY," +
                    "Name       VARCHAR( 100 )," +
                    "Location   VARCHAR( 50 )," +
                    "Frequency  INT," +
                    "UpdateTime DATETIME" +
                    ");";

            this.repository = RepositoryFactory.GetRepository(this, dbName, dbVersion, RepositoryEnum.SQLite3, dbInitString);

            if (this.repository != null) {
                result = true;
            }
        }

        return result;
    }
}