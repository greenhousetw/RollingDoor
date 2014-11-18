/**
 * CommonHelper.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.sharelibs;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bosswiin.SecurityLocker.R;

/**
 * CommonHelper
 * This class provides common using methods
 */
public class CommonHelper {

    /**
     * To translate seconds to milli seconds
     * date: 2014/10/29
     *
     * @param value value of seconds
     * @return milli seconds.
     * @author Yu-Hua Tseng
     */
    public static double SecsToMilliSeconds(double value){
        return value*1000;
    }

    /**
     * Show toast message
     * date: 2014/10/29
     *
     * @param context context in runtime, usually is Activity
     * @param resId message
     * @return milli seconds.
     * @author Yu-Hua Tseng
     */
    public static void ShowToast(Context context, String resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.TEXT_SIZE));
        toast.show();
    }

    /**
     * Show toast message
     * date: 2014/11/18
     *
     * @param context string content
     * @return milli seconds.
     * @author Yu-Hua Tseng
     */
    public static boolean stringIsNullOrEmpty(String context){

        boolean result=false;

        if(context == null || context.length()==0){
            result=true;
        }

        return result;
    }
}
