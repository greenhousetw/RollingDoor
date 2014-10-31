package com.bosswiin.SecurityLocker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.bosswiin.UserInterface.Components.BLEAdapter;
import com.bosswiin.device.bluetooth.BLEAcionEnum;
import com.bosswiin.device.bluetooth.BLERequest;
import com.bosswiin.device.bluetooth.BossWiinBlueToothManager;
import com.bosswiin.repository.IRepository;
import com.bosswiin.repository.RepositoryEnum;
import com.bosswiin.repository.RepositoryFactory;
import com.bosswiin.sharelibs.JSONHelper;
import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener {

    private ListView listView;
    private Button scanButton = null, upButton = null, stopButton = null, downButton = null;
    private IRepository              repository       = null;
    private BLEAdapter               bleAdpater       = null;
    private String                   selectedAddress  = null;
    private BossWiinBlueToothManager blueToothManager = null;
    private BLERequest               bleRequest       = null;
    private String                   tableName        = "DeviceList";
    private Context                  mainContext      = this;
    private HashMap<String, Object>  databaseTuple    = new HashMap<String, Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        BossWiinBlueToothManager.IsHardwareEanble(this);

        this.bleAdpater = new BLEAdapter(this);
        this.blueToothManager = new BossWiinBlueToothManager(this, this.bleAdpater);
        this.bleRequest = new BLERequest();

        super.setContentView(R.layout.main);

        this.listView = (ListView) this.findViewById(R.id.listView);
        this.scanButton = (Button) this.findViewById(R.id.buttonScan);
        this.upButton = (Button) this.findViewById(R.id.buttonUP);
        this.downButton = (Button) this.findViewById(R.id.buttonDown);
        this.stopButton = (Button) this.findViewById(R.id.buttonStop);

        this.scanButton.setOnClickListener(this);
        this.upButton.setOnClickListener(this);
        this.stopButton.setOnClickListener(this);
        this.downButton.setOnClickListener(this);

        this.listView.setSelector(R.drawable.listrowhighlighter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView info = (TextView) view.findViewById(R.id.info);
                selectedAddress = info.getTag().toString();

                bleRequest.actionEnum = BLEAcionEnum.StopScan;
                blueToothManager.Execute(bleRequest);
            }
        });

        this.listView.setAdapter(this.bleAdpater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.GetRepository();
        //this.blueToothManager.Execute(this.deviceAddress, BLEAcionEnum.CheckEquipment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.blueToothManager.Execute(this.deviceAddress, BLEAcionEnum.CheckEquipment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.blueToothManager.Execute(this.deviceAddress, BLEAcionEnum.Diconnect);
        // this.blueToothManager.Execute(this.deviceAddress, BLEAcionEnum.Close);
    }

    @Override
    public void onClick(android.view.View view) {

        if (view.getId() == R.id.buttonScan) {

            this.bleRequest.actionEnum = BLEAcionEnum.Scan;
            this.blueToothManager.Execute(this.bleRequest);
        }
        else if (view.getId() == R.id.buttonUP) {

            this.bleRequest.actionEnum=BLEAcionEnum.Send;
            this.bleRequest.remoteAddress=this.selectedAddress;
            this.bleRequest.serviceUUID="713d0000-503e-4c75-ba94-3148f18d941e";
            this.bleRequest.characteristicsUUID="713d0003-503e-4c75-ba94-3148f18d941e";
            this.bleRequest.transmittedContent="0001";
            this.blueToothManager.Execute(this.bleRequest);
            /*
            this.databaseTuple.put("UUID", UUID.randomUUID().toString());
            this.databaseTuple.put("Name", "Joey");
            this.databaseTuple.put("Location", "1F");
            this.databaseTuple.put("Frequency", Integer.parseInt("0"));

            Time now = new Time();
            now.setToNow();
            this.databaseTuple.put("UpdateTime", now.format("%Y.%m.%d %H:%M:%S"));
            this.repository.Insert(this.tableName, this.databaseTuple);

            this.databaseTuple.clear();
            */
        }
        else if (view.getId() == R.id.buttonDown) {

            this.bleRequest.actionEnum=BLEAcionEnum.Send;
            this.bleRequest.remoteAddress=this.selectedAddress;
            this.bleRequest.serviceUUID="713d0000-503e-4c75-ba94-3148f18d941e";
            this.bleRequest.characteristicsUUID="713d0003-503e-4c75-ba94-3148f18d941e";
            this.bleRequest.transmittedContent="0001";
            this.blueToothManager.Execute(this.bleRequest);
        }
        else if (view.getId() == R.id.buttonStop) {

            this.listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, this.repository.GetTableList()));
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