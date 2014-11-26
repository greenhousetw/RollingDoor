/**
 * BLEAdapter.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.UserInterface.Components;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bosswiin.SecurityLocker.R;
import com.bosswiin.device.bluetooth.BLEDeviceInfo;

/**
 * This class is UI related class, which provides BLEDeviceInfo presentation
 */
public class BLEDBAdapter extends BLEAdpaterBase {

    // tag for logging
    private final String LOGTAG = BLESimpleAdapter.class.getSimpleName();
    // runtime context
    private Context context=null;

    /**
     * Initializes a new instance of the RollingAdpater class.
     * date: 2014/10/24
     *
     * @param activity instance of Activity
     * @author Yu-Hua Tseng
     */
    public BLEDBAdapter(Activity activity) {
        super(activity);
        this.context=activity;
    }

    /**
     * To add new device into a UI component that can accept Adapater
     * date: 2014/11/07
     *
     * @param bleDeviceName name of this Device
     * @param address       address of peripheral
     * @author Yu-Hua Tseng
     */
    @Override
    public void AddNewDevice(final String bleDeviceName, final String address) {
        if (!this.mDevices.containsKey(address)) {
            Log.d(LOGTAG, "New Device, its address=" + address);
            BLEDeviceInfo deviceInfo = new BLEDeviceInfo();
            deviceInfo.deviceAddress = address;
            deviceInfo.deviceName = bleDeviceName;
            deviceInfo.rssi = Integer.toString(0);
            this.mDevices.put(deviceInfo.deviceAddress, deviceInfo);
            Log.v(BLESimpleAdapter.class.getSimpleName(), "Peripheral count=" + this.mDevices.size());
        }
    }

    /**
     * Get record counts in one UI component
     * date: 2014/10/29
     *
     * @author Yu-Hua Tseng
     */
    @Override
    public int getCount() {
        return this.mDevices.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * date: 2014/10/24
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     * @author Yu-Hua Tseng
     */
    @Override
    public Object getItem(int position) {
        return GetDevice(position);
    }

    /**
     * Get the instance of selected BLEDeviceInfo.
     * date: 2014/10/24
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     * @author Yu-Hua Tseng
     */
    public BLEDeviceInfo GetDevice(int position) {
        BLEDeviceInfo defInfo = null;

        try {
            Log.v(LOGTAG, "selected item's position=" + position);
            defInfo=this.mDevices.get(this.mDevices.keySet().toArray()[position]);
            Log.v(BLESimpleAdapter.class.getSimpleName(), "Adapter adds name:" + defInfo.deviceName + ", address=" + defInfo.deviceAddress);

        } catch (Exception e) {
            Log.e(LOGTAG, e.getMessage());
        }

        return defInfo;
    }

    /**
     * Get the row id associated with the specified position in the list.
     * date: 2014/10/24
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The id of the item at the specified position.
     * @author Yu-Hua Tseng
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * You can either create a View manually or inflate it from an XML layout file.
     * When the View is inflated, the parent View (GridView, ListView...) will apply
     * default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean)
     * to specify a root view and to prevent attachment to the root.
     * date: 2014/10/24
     *
     * @param position    Position of the item whose data we want within the adapter's data set.
     * @param convertView The old view to reuse, if possible. Note: You should check that this
     *                    view is non-null and of an appropriate type before using. If it is
     *                    not possible to convert this view to display the correct data, this
     *                    method can create a new view. Heterogeneous lists can specify their
     *                    number of view types, so that this View is always of the right type
     *                    (see getViewTypeCount() and getItemViewType(int)).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     * @author Yu-Hua Tseng
     * @see http://developer.android.com/intl/zh-tw/reference/android/widget/Adapter.html#getCount%28%29
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.rollingitemlayout, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.bleIcon);
            holder.nameField = (TextView) convertView.findViewById(R.id.bleDeviceName);
            holder.rssiField = (TextView) convertView.findViewById(R.id.RssiTextView);
            holder.connectionStatus = (TextView) convertView.findViewById(R.id.bleProgressBar);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // start to create view
        try {
            BLEDeviceInfo record = (BLEDeviceInfo) this.GetDevice(position);
            holder.nameField.setText(record.deviceName);
            holder.rssiField.setText(this.context.getString(R.string.rssiPrefixValue) + "?");
            holder.nameField.setTag(record.deviceAddress);
        } catch (Exception ex) {
            Log.e(LOGTAG, ex.getMessage());
        }

        return convertView;
    }

    /**
     * This class keeps entire view
     */
    private static class ViewHolder {

        // image view in rollingitemlayout
        ImageView image     = null;
        // TextView in rollingitemlayout
        TextView  nameField = null;
        // TextView in rollingitemlayout
        TextView  rssiField = null;
        // Textview for connection status
        TextView connectionStatus=null;
    }
}