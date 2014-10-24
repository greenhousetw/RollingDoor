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

}
