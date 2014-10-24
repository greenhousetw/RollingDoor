package com.bosswiin.device.bluetooth;

import android.util.Log;

/**
 * Created by 9708023 on 2014/10/22.
 */
public class BLEScan extends BLEActionBase {

    public BLEScan(){
    }

    @Override
    public boolean Execute(BLERequest request) {

        boolean result=false;

        if(request.actionEnum != BLEAcionEnum.Scan)
        {
           result=this.successor.Execute(request);
        }
        else
        {
            Log.v(this.getClass().getPackage().getName(), this.getClass().getName());

            String[] mStrings = new String[] {
                    "大餅包小餅", "蚵仔煎", "東山鴨頭", "臭豆腐", "潤餅",
                    "豆花", "青蛙下蛋","豬血糕", "大腸包小腸", "鹹水雞",
                    "烤香腸","車輪餅","珍珠奶茶","鹹酥雞","大熱狗",
                    "炸雞排","山豬肉","花生冰","剉冰","水果冰",
                    "包心粉圓","排骨酥","沙茶魷魚","章魚燒","度小月",
                    "aaa","abc","bbb","bcd","123"
            };

            request.PushData(BLEAcionEnum.Scan, mStrings);
        }

        return result;
    }
}
