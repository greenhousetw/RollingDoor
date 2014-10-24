package com.bosswiin.SecurityLocker;

import com.bosswiin.com.bosswiin.repository.*;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.bosswiin.device.bluetooth.*;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.*;
import android.widget.*;
import android.view.View;
import android.view.View.*;
import android.app.AlertDialog.*;
import android.widget.AdapterView.OnItemClickListener;
import java.util.*;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class MainActivity extends Activity implements OnClickListener {

    private BLERequest bluetoothRequest=new BLERequest();
    private BlueToothManager btManager=new BlueToothManager();
    private ListView listView;
    private Button scanButton=null, upButton=null, stopButton=null, downButton=null;

    private IRepository repository=null;

    private String tableName="DeviceList";
    private Context mainContext=this;
    private HashMap<String, Object> databaseTuple=new HashMap<String, Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this.GetRepository()) {
            super.setContentView(R.layout.main);
            this.listView = (ListView) this.findViewById(R.id.listView);
            this.scanButton = (Button) this.findViewById(R.id.buttonScan);
            this.upButton = (Button) this.findViewById(R.id.buttonUP);
            this.downButton= (Button) this.findViewById(R.id.buttonDown);
            this.stopButton= (Button) this.findViewById(R.id.buttonStop);
            this.scanButton.setOnClickListener(this);
            this.upButton.setOnClickListener(this);
            this.stopButton.setOnClickListener(this);
            this.downButton.setOnClickListener(this);

            this.listView.setSelector(R.drawable.listrowhighlighter);
            this.listView.setOnItemClickListener(new OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String selectedFromList = listView.getItemAtPosition(position).toString();
                    Builder MyAlertDialog = new Builder(mainContext);
                    MyAlertDialog.setTitle("標題");
                    MyAlertDialog.setMessage(selectedFromList);
                    MyAlertDialog.show();

                }});
        }
    }


    @Override
    public void onClick(android.view.View view){

        if(view.getId()==R.id.buttonScan){

            this.databaseTuple.put("UUID", "UUID");
            this.databaseTuple.put("Name", "Name");
            this.databaseTuple.put("Location", "Location");
            this.databaseTuple.put("Frequency", "Frequency");
            this.databaseTuple.put("UpdateTime","UpdateTime");

            this.listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, this.repository.Query(this.tableName, this.databaseTuple)));
        }
        else if(view.getId() == R.id.buttonUP){

            this.databaseTuple.put("UUID", UUID.randomUUID().toString());
            this.databaseTuple.put("Name", "Joey");
            this.databaseTuple.put("Location", "1F");
            this.databaseTuple.put("Frequency", Integer.parseInt("0"));

            Time now = new Time();
            now.setToNow();
            this.databaseTuple.put("UpdateTime",now.format("%Y.%m.%d %H:%M:%S"));
            this.repository.Insert(this.tableName,this.databaseTuple);

            this.databaseTuple.clear();
        }
        else if(view.getId() == R.id.buttonDown){

            this.listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, this.repository.GetTableList()));
        }
        else if(view.getId() == R.id.buttonStop){

            this.listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, this.repository.GetTableList()));
        }
    }

    private boolean GetRepository(){

        boolean result=false;

        if(this.repository == null) {

            String dbName="info.db";
            int dbVersion=1;
            final String dbInitString="CREATE TABLE IF NOT EXISTS DeviceList(" +
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