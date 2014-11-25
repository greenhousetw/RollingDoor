package com.bosswiin.SecurityLocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.bosswiin.UserInterface.Components.BLEAdpaterBase;
import com.bosswiin.UserInterface.Components.BLEDBAdapter;
import com.bosswiin.device.bluetooth.BLEAcionEnum;
import com.bosswiin.device.bluetooth.BLERequest;
import com.bosswiin.device.bluetooth.IJBTManagerUICallback;
import com.bosswiin.device.bluetooth.JBluetoothManager;
import com.bosswiin.devicemanager.ChwanJheDeviceManager;
import com.bosswiin.devicemanager.IJBTDeviceManager;
import com.bosswiin.sharelibs.CommonHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private String                  selectedAddress             = "";
    private String                  tableName                   = "DeviceList";
    private Context                 mainContext                 = this;
    private HashMap<String, Object> databaseTuple               = new HashMap<String, Object>();
    private JBluetoothManager       mJBluetootManager           = null;
    private BLEAdpaterBase          bleAdpater                  = null;
    private BLERequest              request                     = new BLERequest();
    private MainActivity            acts                        = this;
    private int                     currentSelection            = Integer.MAX_VALUE;
    private TextView                currentSelectedTextview     = null;
    private TextView                currentSelectedRSSITextview = null;
    private boolean                 isPauseBack                 = false;
    private boolean                 isDestroyBack               = false;
    private boolean                 isBTHardwareAvaialbe        = false;
    private IJBTDeviceManager mDeviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(this.logTag, "Application in onCreate phase");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        this.mJBluetootManager = new JBluetoothManager(this);

        this.bleAdpater = new BLEDBAdapter(this);
        this.listView = (ListView) this.findViewById(R.id.listView);
        this.listView.setEmptyView(findViewById(R.id.empty));
        this.scanButton = (Button) this.findViewById(R.id.buttonScan);
        this.upButton = (Button) this.findViewById(R.id.buttonUP);
        this.downButton = (Button) this.findViewById(R.id.buttonDown);
        this.stopButton = (Button) this.findViewById(R.id.buttonStop);

        //this.scanButton.setOnClickListener(this);
        this.upButton.setOnClickListener(this);
        this.stopButton.setOnClickListener(this);
        this.downButton.setOnClickListener(this);

        this.acts = this;

        this.listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                view.setSelected(true);

                TextView peripheralName = (TextView) view.findViewById(R.id.bleDeviceName);

                if (selectedAddress != peripheralName.getTag().toString()) {
                    acts.resetSelectedItemUI();
                    selectedAddress = peripheralName.getTag().toString();

                    request.remoteAddress = selectedAddress;
                    request.characteristicsUUID = uuidDoorCharactristicsForRead;
                    request.serviceUUID = uuidDoorService;
                    request.actionEnum = BLEAcionEnum.Notification;

                    mJBluetootManager.resetConnection(selectedAddress);

                    if (mJBluetootManager.checkConnection(selectedAddress)) {
                        mJBluetootManager.changeBleDevice(request);
                        TextView statusText = (TextView) acts.findViewById(R.id.bleProgressBar);
                        statusText.setText(acts.getString(R.string.connectionStringForSuccessful));
                    }
                    else {

                        try {
                            double waitSeconds = 0.8;
                            Thread.sleep((long) waitSeconds);
                            if (!mJBluetootManager.checkConnection(selectedAddress)) {
                                CommonHelper.ShowToast(acts, acts.getString(R.string.DoorHint_RemoteNotConnected));
                            }
                        } catch (Exception ex) {
                            Log.e(logTag, ex.getMessage());
                        }
                    }
                }

                currentSelection = position;
                currentSelectedTextview = (TextView) acts.findViewById(R.id.bleProgressBar);
                currentSelectedRSSITextview = (TextView) acts.findViewById(R.id.RssiTextView);
            }
        });

        this.mDeviceManager = new ChwanJheDeviceManager(this, true);
        listView.setAdapter(this.bleAdpater);
        this.scanButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // restore peripherals into listview
        this.getDoorList();
        Log.d(this.logTag, "Application in start phase");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.logTag, "Application in resume phase");

        if (this.mJBluetootManager.enableBluetoothHardware(this)) {
            this.isBTHardwareAvaialbe = true;
        }

        // if back from "home"
        if (isBTHardwareAvaialbe && !this.isDestroyBack) {
            mJBluetootManager.setBluetoothLowEnergyWrapper(this);
            // shutdown automatic scanning
            //mJBluetootManager.startScanning();
        }

        if (this.isPauseBack) {
            if (this.listView != null && this.currentSelection != Integer.MAX_VALUE) {
                Log.d(logTag, "the index of current peripheral=" + this.currentSelection);
                this.listView.setSelection(this.currentSelection);
                this.listView.setItemsCanFocus(true);
                this.selectedAddress="";
                this.listView.performItemClick(this.listView, this.currentSelection, this.listView.getItemIdAtPosition(this.currentSelection));
            }
        }

        this.isPauseBack = false;
        this.isDestroyBack = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.logTag, "Application in pause phase");

        mJBluetootManager.resetConnection(selectedAddress);

        if (this.currentSelectedTextview != null && this.currentSelectedTextview.getText() == this.getString(R.string.connectionStringForSuccessful)) {
            this.currentSelectedTextview.setText(this.getString(R.string.connectionStringForPause));
        }

        this.isPauseBack = true;
        this.isBTHardwareAvaialbe = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.logTag, "Application in stop phase");

        if (this.currentSelectedTextview != null) {
            this.currentSelectedTextview.setText(this.getString(R.string.connectionStatus));
            this.currentSelectedRSSITextview.setText(this.getText(R.string.rssiPrefixValue) + "?");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.logTag, "Application in destroy phase");
        this.isDestroyBack = true;
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.app_name))
                .setMessage(this.getString(R.string.exitApp))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                        acts.finish();
                        System.exit(0);
                    }
                }).create().show();
    }

    @Override
    public void onClick(android.view.View view) {

        try {

            String chatService = "up";
            this.request.actionEnum = BLEAcionEnum.ChungJeSend;
            this.request.serviceUUID = this.uuidDoorService;
            this.request.remoteAddress = selectedAddress;

            if (view.getId() == R.id.buttonScan) {

                //this.mJBluetootManager.stopScanning();
                this.mJBluetootManager.startScanning();
            }
            else if (this.request.remoteAddress.length() != 0) {

                if (this.mJBluetootManager.checkConnection(this.request.remoteAddress)) {

                    if (view.getId() == R.id.buttonUP) {

                        this.request.characteristicsUUID = this.uuidDoorCharactristicsForUP;
                        //this.request.transmittedContent = chatService.getBytes();
                        this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00};
                        this.mJBluetootManager.executeRequest(this.request);
                    }
                    else if (view.getId() == R.id.buttonDown) {

                        this.request.characteristicsUUID = this.uuidDoorCharactristicsForDown;
                        //chatService = "down";
                        //this.request.transmittedContent = chatService.getBytes();
                        this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x03, (byte) 0x00};
                        this.mJBluetootManager.executeRequest(this.request);
                    }
                    else if (view.getId() == R.id.buttonStop) {

                        this.request.characteristicsUUID = this.uuidDoorCharactristicsForStop;
                        //chatService = "stop";
                        this.request.transmittedContent = chatService.getBytes();
                        this.request.transmittedContent = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x00};
                        this.mJBluetootManager.executeRequest(this.request);
                    }
                }
                else {
                    CommonHelper.ShowToast(this, this.getString(R.string.DoorHint_IsNotConnected));
                    this.resetSelectedItemUI();
                    this.listView.removeViewAt(currentSelection);
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
                if (deviceName != null && deviceName.length() != 0) {
                    bleAdpater.AddNewDevice(deviceName, address);
                    bleAdpater.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Pass data to activity for ui related operations
     * date: 2014/11/24
     * date: 2014/11/09
     *
     * @param data data that needs to be process
     * @author Yu-Hua Tseng
     * @note 2014/11/24:
     * if one device is in disconnection status, below code will be executed from handleDeviceDisconnected of
     * JBluetoothManager
     * if (value == this.getString(R.string.connectionStringForUnknown))
     */
    @Override
    public void passContentToActivity(Object data) {
        if (data != null) {

            if (data instanceof String) {

                String value = data.toString();

                if (value.startsWith(this.getString(R.string.rssiPrefixValue))) {
                    ((TextView) this.findViewById(R.id.RssiTextView)).setText(value + " db");
                    ((TextView) this.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStringForSuccessful);
                }
                else if (value == this.getString(R.string.connectionStatus)) {
                    if (this.currentSelectedTextview != null) {
                        this.currentSelectedTextview.setText(this.getString(R.string.connectionStatus));
                        ((TextView) this.findViewById(R.id.RssiTextView)).setText(this.getString(R.string.rssiPrefixValue) + "?");
                    }
                }
                else {
                    ((EditText) this.findViewById(R.id.editnoticontent)).setText(value.trim());
                    (this.findViewById(R.id.editnoticontent)).clearFocus();
                    this.listView.requestFocus();
                }
            }
        }
    }

    /**
     * Reset selected item ui to default
     * date: 2014/11/14
     *
     * @author Yu-Hua Tseng
     */
    private void resetSelectedItemUI() {

        if (this.currentSelectedTextview != null && this.currentSelectedRSSITextview != null) {
            this.currentSelectedTextview.setText(acts.getString(R.string.connectionStatus));
            this.currentSelectedRSSITextview.setText(acts.getString(R.string.rssiPrefixValue) + "?");
        }
    }

    /**
     * This is trail method
     * date: 2014/11/10
     *
     * @author Yu-Hua Tseng
     */
    private boolean getDoorList() {

        boolean result = false;

        // set column name
        String columnAddress = "address";
        String columnName = "name";
        String columnLocation = "location";
        String columnFrequency = "frequency";
        String columnUpdateTime = "updatetime";
        this.databaseTuple.put(columnAddress, columnAddress);
        this.databaseTuple.put(columnName, columnName);
        this.databaseTuple.put(columnLocation, columnLocation);
        this.databaseTuple.put(columnFrequency, columnFrequency);
        this.databaseTuple.put(columnUpdateTime, columnUpdateTime);

        JSONArray array = this.mDeviceManager.getDeviceList(this.tableName, this.databaseTuple);

        // add new device into device list
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject record = array.getJSONObject(i);
                this.addNewDevices(record.get("name").toString(),
                        record.get("address").toString(),
                        0,
                        null);
            } catch (JSONException jsonEx) {
                Log.e(this.logTag, jsonEx.getMessage());
            }

        }

        return result;
    }
}