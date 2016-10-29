package com.skey.mygoogleplay.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;

import com.skey.mygoogleplay.global.GooglePlayApplication;

/**
 * UI常用工具类
 *
 * @author ALion on 2016/10/13 20:58
 */
public class UIUtils {

    public static Context getContext() {
        return GooglePlayApplication.getContext();
    }

    public static Handler getHandler() {
        return GooglePlayApplication.getHandler();
    }

    public static int getMainTid() {
        return GooglePlayApplication.getMainTid();
    }

    //----------------加载资源文件----------------
    //获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    //获取颜色
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);//(像素值)
    }

    //----------------dip和px的转换----------------
    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;//设备密度
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    //----------------加载布局文件----------------
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    //----------------判断是否运行在主线程----------------
    public static boolean isRunOnUIThread() {
        return getMainTid() == Process.myTid();//比较主线程id和当前线程id，是否一致
    }

    //运行在主线程
    public static void runOnUIThread(Runnable runnable) {
        if (isRunOnUIThread())
            runnable.run();//已经是在主线程，直接运行
        else
            getHandler().post(runnable);//如果是子线程，借助handler让其运行在主线程
    }


}
