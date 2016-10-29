package com.skey.mygoogleplay.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

/**
 * 自定义Application, 进行全局初始化
 *
 * @author ALion on 2016/10/13 20:46
 */
public class GooglePlayApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainTid;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        handler = new Handler();
        mainTid = Process.myTid();//当前线程id，此处是主线程
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainTid() {
        return mainTid;
    }
}
