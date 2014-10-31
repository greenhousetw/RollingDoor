/**
 * JSONHelper.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.sharelibs;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * JSONHelper
 * This class provides JSON related oeprations
 */
public class JSONHelper {

    // log tag for logging
    private final static String LOGNAME = "JSONHELPER";

    /**
     * According to the given json string to JSON Array
     * like:
     * {"xx":"xx","xx:"yy"} to JSONArray
     * date: 2014/10/24
     *
     * @param list json string
     * @return JSON Array
     * @author Yu-Hua Tseng
     */
    public static JSONArray GetJSON(ArrayList<String> list) {
        JSONArray array = null;

        try {
            Log.i(LOGNAME, "Record size=" + Integer.toString(list.size()));

            array = new JSONArray();

            for (String element : list) {
                JSONObject objectData = new JSONObject(element);
                array.put(objectData);
            }

        } catch (Exception ex) {
            Log.e(LOGNAME, ex.getMessage());
        }

        return array;
    }

    /**
     * To JSON by given Map class, which is inheritance from Map
     * date: 2014/10/24
     *
     * @param map the instance of Map inheritance class
     * @return JSON Object
     * @author Yu-Hua Tseng
     */
    public static JSONObject GetJSON(Map map) {

        Iterator iteration = map.entrySet().iterator();

        JSONObject holder = new JSONObject();

        while (iteration.hasNext()) {
            Map.Entry pairs = (Map.Entry) iteration.next();
            String key = (String) pairs.getKey();
            Map m = (Map) pairs.getValue();
            JSONObject data = new JSONObject();

            try {
                Iterator iterationNext = m.entrySet().iterator();
                while (iterationNext.hasNext()) {
                    Map.Entry pairs2 = (Map.Entry) iterationNext.next();
                    data.put((String) pairs2.getKey(), (String) pairs2.getValue());
                }
                holder.put(key, data);
            } catch (JSONException e) {
                Log.e("Transforming", "There was an error packaging JSON", e);
            }
        }

        return holder;
    }
}
