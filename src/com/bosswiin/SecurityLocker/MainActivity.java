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

import java.util.HashMap;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener, IJBTManagerUICallback {

    private final String uuidDoorService = "713d0000-503e-4c75-ba94-3148f18d941e";
    private final String uuidDoorCharactristicsForRead = "713d0002-503e-4c75-ba94-3148f18d941e";
    private final String uuidDoorCharactristicsForUP = "713d0003-503e-4c75-ba94-3148f18d941e";
    private final String uuidDoorCharactristicsForDown = "713d0003-503e-4c75-ba94-3148f18d941e";
    private final String uuidDoorCharactristicsForStop = "713d0003-503e-4c75-ba94-3148f18d941e";

    private ListView listView = null;
    private Button scanButton = null, upButton = null, stopButton = null, downButton = null;
    private IRepository repository = null;

    private String selectedAddress = "";

    private String tableName = "DeviceList";
    private Context mainContext = this;
    private HashMap<String, Object> databaseTuple = new HashMap<String, Object>();

    private JBluetoothManager mJBluetootManager = null;
    private BLEAdpaterBase bleAdpater = null;
    private BLERequest request = new BLERequest();

    private MainActivity activity = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        this.mJBluetootManager = new JBluetoothManager(this);
        this.mJBluetootManager.setBluetoothLowEnergyWrapper(this);

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
        //this.GetRepository();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(android.view.View view) {

        try {

            String chatService = "up";
            this.request.actionEnum = BLEAcionEnum.Send;
            this.request.serviceUUID = this.uuidDoorService;
            this.request.remoteAddress = selectedAddress;

            if (view.getId() == R.id.buttonScan) {

                this.mJBluetootManager.stopScanning();
                this.mJBluetootManager.startScanning();

            } else if (this.request.remoteAddress.length() != 0) {

                if (view.getId() == R.id.buttonUP) {

                    this.request.characteristicsUUID = this.uuidDoorCharactristicsForUP;
                    //this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00};
                    this.request.transmittedContent = chatService.getBytes();
                    this.mJBluetootManager.executeRequest(this.request);

                } else if (view.getId() == R.id.buttonDown) {

                    this.request.characteristicsUUID = this.uuidDoorCharactristicsForDown;
                    chatService = "down";
                    this.request.transmittedContent = chatService.getBytes();
                    //this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00};
                    this.mJBluetootManager.executeRequest(this.request);
                } else if (view.getId() == R.id.buttonStop) {

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
     * @param address address of peripheral
     * @param rssi signal strength of peripheral
     * @param record other record of peripheral
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
    public void passContentToActivity(Object data)
    {
        if(data != null){
            ((EditText)this.findViewById(R.id.editnoticontent)).setText(data.toString().trim());
            ((EditText)this.findViewById(R.id.editnoticontent)).clearFocus();
            this.listView.requestFocus();
        }
    }

    private void InitDoorList() {

        // set column name
        this.databaseTuple.put("UUID", "UUID");
        this.databaseTuple.put("Name", "Name");
        this.databaseTuple.put("Location", "Location");
        this.databaseTuple.put("Frequency", "Frequency");
        this.databaseTuple.put("UpdateTime", "UpdateTime");

        JSONArray array = JSONHelper.GetJSON(this.repository.Query(this.tableName, this.databaseTuple));
    }

    private boolean GetRepository() {

        boolean result = false;

        if (this.repository == null) {

            String dbName = "info.db";
            int dbVersion = 1;
            final String dbInitString = "CREATE TABLE IF NOT EXISTS DeviceList(" +
                    "UUID       VARCHAR( 60 )   PRIMARY KEY," +
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