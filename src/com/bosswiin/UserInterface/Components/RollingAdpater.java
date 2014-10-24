/**
 * This class is the specific adpater for json content and also inherits from BaseAdpater
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since   0.0
 */
package com.bosswiin.UserInterface.Components;

import android.content.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.BaseAdapter;
import com.bosswiin.SecurityLocker.R;
import org.json.*;

/**
 * RollingAdpater inherits from BaseAdpater
 */
public class RollingAdpater extends BaseAdapter
{
    private final String LOGTAG="RollingAdapter";

    // Context in runtime
    private Context context = null;

    // Instantiates a layout XML file into its corresponding View objects
    private LayoutInflater layoutInflater = null;

    // list data in JONArray format
    private JSONArray array = null;

    /**
     * Initializes a new instance of the RollingAdpater class.
     * date: 2014/10/24
     * @author Yu-Hua Tseng
     * @param  context  instance of component for adpater creation
     * @param  jsonArray  data collection
     */
    public RollingAdpater(Context context, JSONArray jsonArray)
    {
        this.context=context;
        this.array=jsonArray;
        this.layoutInflater=LayoutInflater.from(this.context);
    }

    /**
     * This class keeps entire view
     */
    //private static class ViewHolder
    private static class ViewHolder
    {
        ImageView image=null;

        TextView nameField=null;

        TextView infoField=null;

        String equipmentUUID="";
    }

    /**
     * How many items are in the data set represented by this Adapter.
     * date: 2014/10/24
     * @author Yu-Hua Tseng
     * @return Count of items
     */
    @Override
    public int getCount()
    {
        return array.length();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * date: 2014/10/24
     * @author Yu-Hua Tseng
     * @param  position  Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        try
        {
            return array.getString(position);
        }
        catch (JSONException e)
        {
            return null;
        }
    }

    /**
     * Get the row id associated with the specified position in the list.
     * date: 2014/10/24
     * @author Yu-Hua Tseng
     * @param  position  Position of the item whose data we want within the adapter's data set.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * You can either create a View manually or inflate it from an XML layout file.
     * When the View is inflated, the parent View (GridView, ListView...) will apply
     * default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean)
     * to specify a root view and to prevent attachment to the root.
     * date: 2014/10/24
     * @author Yu-Hua Tseng
     * @param  position  Position of the item whose data we want within the adapter's data set.
     * @param  convertView The old view to reuse, if possible. Note: You should check that this
     *                     view is non-null and of an appropriate type before using. If it is
     *                     not possible to convert this view to display the correct data, this
     *                     method can create a new view. Heterogeneous lists can specify their
     *                     number of view types, so that this View is always of the right type
     *                     (see getViewTypeCount() and getItemViewType(int)).
     * @param  parent  The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     * @see http://developer.android.com/intl/zh-tw/reference/android/widget/Adapter.html#getCount%28%29
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = this.layoutInflater.inflate(R.layout.rollingitemlayout, parent, false);
            holder = new ViewHolder();
            holder.image=(ImageView) convertView.findViewById(R.id.img);
            holder.nameField = (TextView) convertView.findViewById(R.id.name);
            holder.infoField = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            JSONObject record = (JSONObject) array.get(position);
            holder.image.setBackgroundResource((Integer) R.drawable.door2);
            holder.nameField.setText(record.get("Name").toString());
            holder.infoField.setText(record.get("Location").toString());
            holder.infoField.setTag(record.get("UUID").toString());
        }
        catch (JSONException ex)
        {
            Log.e(LOGTAG, ex.getMessage());
        }

        return convertView;
    }
}
