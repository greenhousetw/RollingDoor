package com.bosswiin.sharelibs;

import android.content.Context;
import android.app.Activity;

/**
 * Created by 9708023 on 2014/10/24.
 */
public class ContextHelper {

    private static Context innerContext=null;

    private static Activity activity=null;

    public synchronized static boolean SetGlobalContext(Context context)
    {
        boolean result=false;

        if(innerContext == null)
        {
            innerContext=context;
            result=true;
        }

        return result;
    }

    public static Context GetGlobalContext()
    {
        return innerContext;
    }
}
