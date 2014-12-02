/**
 * ScreenHelper.java
 * @author Yu-Hua Tseng
 * @version 0.1
 * @since 0.0
 */
package com.bosswiin.sharelibs;

import android.content.Context;

/**
 * ScreenHelper
 * This class provides common using methods for screen
 */
public class ScreenHelper {

    /**
     * Conver dp to px
     * date: 2014/12/02
     *
     * @param context context in runtime, usually is Activity
     * @param dp the value of dp
     * @return pixel value
     * @author Yu-Hua Tseng
     */
    public int ConvertDp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
