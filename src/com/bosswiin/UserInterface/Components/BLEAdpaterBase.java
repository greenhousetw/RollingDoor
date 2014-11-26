/**
 * BLEAdpaterBase.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.UserInterface.Components;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.bosswiin.device.bluetooth.BLEDeviceInfo;

import java.util.LinkedHashMap;

/**
 * BLEAdpaterBase
 * This class is a adpater class which used by UI component
 */
public class BLEAdpaterBase extends BaseAdapter {

    protected LinkedHashMap<String, BLEDeviceInfo> mDevices;
    protected LayoutInflater                       mInflater;

    public BLEAdpaterBase(Activity activity) {
        this.mDevices = new LinkedHashMap<String, BLEDeviceInfo>();
        this.mInflater = activity.getLayoutInflater();
    }

    /**
     * To add new device into a UI component that can accept Adapater
     * date: 2014/11/07
     *
     * @param bleDeviceName name of this Device
     * @param address       address of peripheral
     * @author Yu-Hua Tseng
     */
    public void AddNewDevice(final String bleDeviceName, final String address)
    {
        Log.e(BLEAdpaterBase.class.getSimpleName(), "You should not call this method directly");
    }

    /**
     * To add new device into a UI component that can accept Adapater
     * date: 2014/10/29
     *
     * @param device     instance of BluetoothDevice
     * @param rssi       signal strength
     * @param scanRecord the record of scanning
     * @author Yu-Hua Tseng
     */
    public void AddNewDevice(final BluetoothDevice device,
                             final int rssi,
                             final byte[] scanRecord) {
        Log.e(this.getClass().getName(), "You should not call this method directly");
    }

    /**
     * How many items are in the data set represented by this Adapter.
     * date: 2014/10/24
     *
     * @return Count of items
     * @author Yu-Hua Tseng
     */
    @Override
    public int getCount() {
        return 0;
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
     * Get the data item associated with the specified position in the data set.
     * date: 2014/10/24
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     * @author Yu-Hua Tseng
     */
    @Override
    public Object getItem(int position) {
        return null;
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
        return null;
    }
}

