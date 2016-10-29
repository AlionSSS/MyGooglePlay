package com.skey.mygoogleplay.domain;

import java.util.ArrayList;

/**
 * 首页应用信息封装
 *
 * @author ALion on 2016/10/18 0:19
 */

public class AppInfo {
    public String des;
    public String downloadUrl;
    public String iconUrl;
    public String id;
    public String name;
    public String packageName;
    public long size;
    public float stars;

    //补充字段，供应用详情页使用
    public String author;
    public String date;
    public String downloadNum;
    public ArrayList<SafeInfo> safe;
    public ArrayList<String> screen;
    public String version;

    //当一个内部类是static时，和外部类没有区别
    public static class SafeInfo {
        public String safeDes;
        public String safeDesUrl;
        public String safeUrl;
    }
}
