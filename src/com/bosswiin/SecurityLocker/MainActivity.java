package com.bosswiin.SecurityLocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bosswiin.device.bluetooth.*;
import com.bosswiin.device.bluetooth.ChwanJhe.CJBLEHandler;
import com.bosswiin.devicemanager.ChwanJheDeviceManager;
import com.bosswiin.devicemanager.IJBTDeviceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener, IJBTManagerUICallback {

    private static final int REQUEST_ENABLE_BT = 1;
    private final String uuidDoorService = "713d0000-503e-4c75-ba94-3148f18d941e";
    private final String uuidDoorCharactristicsForWrite = "713d0003-503e-4c75-ba94-3148f18d941e";

    private final String logTag = MainActivity.class.getName();
    private ListView listView = null;
    private Button scanButton = null, upButton = null, stopButton = null, downButton = null;
    private String selectedAddress = "";
    private String tableName = "DeviceList";
    private HashMap<String, Object> databaseTuple = new HashMap<String, Object>();
    private JBluetoothManager mJBluetootManager = null;
    private BLEAdpaterBase bleAdpater = null;

    private MainActivity acts = this;
    private TextView lastSelectedTextview = null;
    private TextView currentSelectedRSSITextview = null;
    private View selectedPeripheral = null;
    private boolean isPauseBack = false;
    private boolean isDestroyBack = false;
    private boolean isBTHardwareAvaialbe = false;
    private IJBTDeviceManager mDeviceManager;
    private IBLEHandler mBleHandler = null;

    private UUID serviceName = UUID.fromString(this.uuidDoorService);
    private UUID chName = UUID.fromString(this.uuidDoorCharactristicsForWrite);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(this.logTag, "Application in onCreate phase");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        this.bleAdpater = new BLEDBAdapter(this);
        this.listView = (ListView) this.findViewById(R.id.listView);
        this.listView.setEmptyView(findViewById(R.id.empty));
        this.listView.setAdapter(this.bleAdpater);
        this.scanButton = (Button) this.findViewById(R.id.buttonScan);
        this.upButton = (Button) this.findViewById(R.id.buttonUP);
        this.downButton = (Button) this.findViewById(R.id.buttonDown);
        this.stopButton = (Button) this.findViewById(R.id.buttonStop);

        //this.scanButton.setOnClickListener(this);
        this.scanButton.setVisibility(View.GONE);
        this.upButton.setOnClickListener(this);
        this.stopButton.setOnClickListener(this);
        this.downButton.setOnClickListener(this);

        this.acts = this;

        this.mDeviceManager = new ChwanJheDeviceManager(this, true);
        this.getDoorList();

        this.mBleHandler = new CJBLEHandler(this, this);

        this.listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                TextView peripheralName = (TextView) view.findViewById(R.id.bleDeviceName);

                if(lastSelectedTextview!=null){
                    lastSelectedTextview.setText(R.string.connectionStatus);
                }

                if(mBleHandler.connect(peripheralName.getTag().toString()))
                {
                    ((TextView)view.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStringForSuccessful);
                    lastSelectedTextview=((TextView)view.findViewById(R.id.bleProgressBar));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.logTag, "Application in resume phase");
        this.mBleHandler.initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.logTag, "Application in pause phase");

        mJBluetootManager.resetConnection(selectedAddress);

        if (this.lastSelectedTextview != null && this.lastSelectedTextview.getText() == this.getString(R.string.connectionStringForSuccessful)) {
            this.lastSelectedTextview.setText(this.getString(R.string.connectionStringForPause));
        }

        this.isPauseBack = true;
        this.isBTHardwareAvaialbe = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.logTag, "Application in stop phase");
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
            if (view.getId() == R.id.buttonUP) {
                this.mBleHandler.writeData(this.serviceName, this.chName, new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00});
            } else if (view.getId() == R.id.buttonDown) {
                this.mBleHandler.writeData(this.serviceName, this.chName,new byte[]{(byte) 0x01, (byte) 0x03, (byte) 0x00});
            } else if (view.getId() == R.id.buttonStop) {
                this.mBleHandler.writeData(this.serviceName, this.chName,new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x00});
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
                    if (this.selectedPeripheral != null) {
                        ((TextView) this.selectedPeripheral.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStringForSuccessful);
                        ((TextView) this.selectedPeripheral.findViewById(R.id.RssiTextView)).setText(value + " db");
                    }
                } else if (value == this.getString(R.string.connectionStatus)) {
                    if (this.selectedPeripheral != null) {
                        ((TextView) this.selectedPeripheral.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStatus);
                        ((TextView) this.selectedPeripheral.findViewById(R.id.RssiTextView)).setText(this.getString(R.string.rssiPrefixValue) + "?");
                    }
                } else {
                    ((EditText) this.findViewById(R.id.editnoticontent)).setText(value.trim());
                    (this.findViewById(R.id.editnoticontent)).clearFocus();
                    this.listView.requestFocus();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Reset selected item ui to default
     * date: 2014/11/14
     *
     * @author Yu-Hua Tseng
     */
    private void resetSelectedItemUI() {

        String connectionHintTitle = this.getString(R.string.connectionStatus);
        String rssiValue = this.getString(R.string.rssiPrefixValue) + "?";

        if (this.lastSelectedTextview != null && this.currentSelectedRSSITextview != null) {
            lastSelectedTextview.setText(connectionHintTitle);
            currentSelectedRSSITextview.setText(rssiValue);
        }

        if (this.selectedPeripheral != null) {
            ((TextView) this.selectedPeripheral.findViewById(R.id.bleProgressBar)).setText(connectionHintTitle);
            ((TextView) this.selectedPeripheral.findViewById(R.id.RssiTextView)).setText(rssiValue);
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