package com.skey.mygoogleplay.utils;

import com.lidroid.xutils.BitmapUtils;

/**
 * BitmapUtils单例模式
 *
 * @author ALion on 2016/10/18 23:05
 */

public class BitmapHelper {

    private static BitmapUtils mBitmapUtils = null;

    //单例，懒汉模式
    public static BitmapUtils getBitmapUtils() {
        if (mBitmapUtils == null) {
            synchronized (BitmapHelper.class) {
                if (mBitmapUtils == null)
                    mBitmapUtils = new BitmapUtils(UIUtils.getContext());
            }
        }
        return mBitmapUtils;
    }
}
