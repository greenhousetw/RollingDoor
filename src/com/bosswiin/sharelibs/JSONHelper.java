package com.bosswiin.sharelibs;

import java.util.*;

import android.util.Log;
import org.json.*;

/**
 * Created by 9708023 on 2014/10/24.
 */
public class JSONHelper {

    private final static String LOGNAME="JSONHELPER";

    public static JSONArray GetJSON(ArrayList<String> list)
    {
        JSONArray array=null;

        try
        {
            Log.i(LOGNAME, "Record size=" + Integer.toString(list.size()));

            array=new JSONArray();

            for(String element: list)
            {
                JSONObject objectData=new JSONObject(element);
                array.put(objectData);
            }

        }
        catch (Exception ex)
        {
            Log.e(LOGNAME, ex.getMessage());
        }

        return array;
    }

    public static JSONObject getJSON(Map map) {

        Iterator iter = map.entrySet().iterator();

        JSONObject holder = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            String key = (String) pairs.getKey();
            Map m = (Map) pairs.getValue();
            JSONObject data = new JSONObject();

            try {
                Iterator iter2 = m.entrySet().iterator();
                while (iter2.hasNext()) {
                    Map.Entry pairs2 = (Map.Entry) iter2.next();
                    data.put((String) pairs2.getKey(), (String) pairs2.getValue());
                }
                holder.put(key, data);
            } catch (JSONException e) {
                Log.e("Transforming", "There was an error packaging JSON",e);
            }
        }

        return holder;
    }
}
