package com.bosswiin.SecurityLocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.bosswiin.UserInterface.Components.BLEAdpaterBase;
import com.bosswiin.UserInterface.Components.BLEDBAdapter;
import com.bosswiin.device.bluetooth.CJBLEHandler;
import com.bosswiin.device.bluetooth.IBLEHandler;
import com.bosswiin.device.bluetooth.IJBTManagerUICallback;
import com.bosswiin.devicemanager.ChwanJheDeviceManager;
import com.bosswiin.devicemanager.IJBTDeviceManager;
import com.bosswiin.sharelibs.CommonHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener, IJBTManagerUICallback {

    private static final int      REQUEST_ENABLE_BT              = 1;
    private final        String   uuidDoorService                = "713d0000-503e-4c75-ba94-3148f18d941e";
    private              UUID     serviceName                    = UUID.fromString(this.uuidDoorService);
    private final        String   uuidDoorCharactristicsForWrite = "713d0003-503e-4c75-ba94-3148f18d941e";
    private              UUID     chName                         = UUID.fromString(this.uuidDoorCharactristicsForWrite);
    private final        String   logTag                         = MainActivity.class.getName();
    private              ListView listView                       = null;
    private              Button   scanButton                     = null, upButton = null, stopButton = null, downButton = null;
    private String                  tableName     = "DeviceList";
    private HashMap<String, Object> databaseTuple = new HashMap<String, Object>();

    private BLEAdpaterBase bleAdpater = null;
    private MainActivity   acts       = this;

    private TextView lastSelectedTextview     = null;
    private TextView lastSelectedRSSITextview = null;
    private View     selectedDevice           = null, currentSelecteDevice = null;

    private IJBTDeviceManager mDeviceManager;
    private IBLEHandler mBleHandler = null;

    private int                         currentIndex = Integer.MIN_VALUE;
    private List<View>                  viewList     = new LinkedList<View>();
    private BluetoothGattCharacteristic chInstance   = null;

    private boolean mPauseFlag = false;
    private boolean isEnableBT = false;

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

                if (mPauseFlag && selectedDevice != null) {
                    view = selectedDevice;
                }

                currentSelecteDevice = view;
                TextView peripheralName = (TextView) view.findViewById(R.id.bleDeviceName);

                if (lastSelectedTextview != null && lastSelectedRSSITextview != null) {
                    acts.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastSelectedTextview.setText(R.string.connectionStatusUnConnection);
                            lastSelectedRSSITextview.setText(acts.getString(R.string.rssiPrefixValue) + "?");
                        }
                    });
                }

                acts.viewList.add(acts.downButton);
                acts.viewList.add(acts.upButton);
                acts.viewList.add(acts.stopButton);
                acts.setUIComponentEnable(acts.viewList, false);

                chInstance = mBleHandler.connect(peripheralName.getTag().toString(),
                        UUID.fromString(uuidDoorService),
                        UUID.fromString(uuidDoorCharactristicsForWrite));

                if (chInstance != null) {
                    ((TextView) view.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStatusConnected);
                    selectedDevice = view;
                    lastSelectedTextview = ((TextView) view.findViewById(R.id.bleProgressBar));
                    lastSelectedRSSITextview = ((TextView) view.findViewById(R.id.RssiTextView));

                    String connectionString = acts.getString(R.string.DoorHint_RemoteConnectionOK);

                    if (mPauseFlag) {
                        connectionString = acts.getString(R.string.DoorHint_RemoteReConnectionOK);
                    }

                    CommonHelper.ShowToast(acts, connectionString);
                }
                else {
                    ((TextView) view.findViewById(R.id.bleProgressBar)).setText(R.string.connectionStatusUnConnection);
                    ((TextView) view.findViewById(R.id.RssiTextView)).setText(acts.getString(R.string.rssiPrefixValue) + "?");
                    CommonHelper.ShowToast(acts, acts.getString(R.string.DoorHint_RemoteNotConnected));
                    selectedDevice = null;
                }

                currentIndex = position;
                acts.viewList.add(acts.downButton);
                acts.viewList.add(acts.upButton);
                acts.viewList.add(acts.stopButton);
                acts.setUIComponentEnable(acts.viewList, true);
            }
        });

        this.setBluetooth(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.logTag, "Application in resume phase");

        this.setBluetooth(true);

        if (this.mBleHandler.initializeBTAdapter()) {
            if (this.mPauseFlag) {
                if (this.currentIndex != Integer.MIN_VALUE) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(currentIndex);
                            listView.performItemClick(listView, currentIndex, listView.getItemIdAtPosition(currentIndex));
                        }
                    });
                }
            }
        }
        else {
            CommonHelper.ShowToast(this, this.getString(R.string.initializeAdapterFail));
            finish();
        }

        this.mPauseFlag = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.logTag, "Application in pause phase");

        if (this.lastSelectedTextview != null && this.lastSelectedTextview.getText() == this.getString(R.string.connectionStringForSuccessful)) {
            this.lastSelectedTextview.setText(this.getString(R.string.connectionStringForPause));
        }

        this.mPauseFlag = true;
        this.mBleHandler.closeConnection();
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
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).create().show();
    }

    @Override
    public void onClick(android.view.View view) {

        try {

            if (this.chInstance != null) {
                if (view.getId() == R.id.buttonUP) {
                    this.mBleHandler.writeData(this.chInstance, new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x00});
                }
                else if (view.getId() == R.id.buttonDown) {
                    this.mBleHandler.writeData(this.chInstance, new byte[]{(byte) 0x01, (byte) 0x03, (byte) 0x00});
                }
                else if (view.getId() == R.id.buttonStop) {
                    this.mBleHandler.writeData(this.chInstance, new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x00});
                }
            }
            else {
                CommonHelper.ShowToast(this, this.getString(R.string.doorReconnect));
            }
        } catch (Exception ex) {
            Log.e(MainActivity.class.getName(), ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
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
     * Callback method for disconnection
     * date: 2014/11/09
     *
     * @param address the address of remote device
     * @author Yu-Hua Tseng
     */
    @Override
    public void uiDeviceDisconnected(String address) {

        if (this.selectedDevice != null) {

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (selectedDevice != null) {
                        ((TextView) selectedDevice.findViewById(R.id.bleProgressBar))
                                .setText(acts.getText(R.string.connectionStatusUnConnection));
                        ((TextView) selectedDevice.findViewById(R.id.RssiTextView))
                                .setText(acts.getString(R.string.rssiPrefixValue) + "?");
                    }
                }
            });
        }
    }

    /**
     * Callback method for write data to remote device
     * date: 2014/11/09
     *
     * @param address         the address of remote device
     * @param chName          uuid value of the target characteristic
     * @param description     characteristic's description
     * @param operationresult result of this operation
     * @author Yu-Hua Tseng
     */
    @Override
    public void uiWriteResult(String address, String chName, String description, boolean operationresult) {
        if (operationresult) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonHelper.ShowToast(acts, "動作成功");
                }
            });
        }
    }

    /**
     * Callback method for write data to remote device
     * date: 2014/11/09
     *
     * @param address the address of remote device
     * @param rssi    the value of current ssi
     * @author Yu-Hua Tseng
     */
    @Override
    public void uiNewRssiAvailable(String address, final int rssi) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String value = Integer.toString(rssi);
                if (lastSelectedRSSITextview != null) {
                    lastSelectedRSSITextview.setText(acts.getString(R.string.rssiPrefixValue) + value + " db");
                }
            }
        });
    }

    /**
     * callback function for notification of new data ready in remote characteristic
     * date: 2014/11/09
     *
     * @param strValue  the value of characteristic
     * @param intValue  the value of characteristic
     * @param rawValue  the value of characteristic
     * @param timestamp time stamp of this data
     * @author Yu-Hua Tseng
     */
    public void uiNewValueForCharacteristic(final String strValue,
                                            final int intValue,
                                            final byte[] rawValue,
                                            final String timestamp) {
    }

    /**
     * enable bluetooth device
     * date: 2014/11/09
     *
     * @param enable the value of characteristic
     * @author Yu-Hua Tseng
     */
    private boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }


    private void setUIComponentEnable(final List<View> viewList, final boolean flag) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (View view : viewList) {
                    view.setEnabled(flag);
                }
            }
        });
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