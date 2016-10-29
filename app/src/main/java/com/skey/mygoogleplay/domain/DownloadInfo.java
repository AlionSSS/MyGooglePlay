package com.skey.mygoogleplay.domain;

import android.os.Environment;

import com.skey.mygoogleplay.manager.DownloadManager;

import java.io.File;

/**
 * 下载对象
 * 【权限】: <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * @author ALion on 2016/10/25 21:14
 */

public class DownloadInfo {

    public String id;
    public String name;
    public String downloadUrl;
    public String packageName;
    public long size;

    public long currentPos;//当前下载的位置
    public int currentState;//当前下载的状态

    public String path;//下载的本地路径

    public static final String GOOGLE_MARKET = "GoogleMarket";//sdcard根目录文件夹名称
    public static final String DOWNLOAD = "Download";//子文件夹名称，存放下载的文件

    /**
     * 获取下载进度（0-1）
     */
    public float getProgress() {
        if (size == 0) {//被除数不能为0
            return 0;
        }
        return (float) currentPos / size;
    }

    /**
     * 拷贝AppInfo，并生成DownloadInfo
     */
    public static DownloadInfo copy(AppInfo info) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.id = info.id;
        downloadInfo.name = info.name;
        downloadInfo.downloadUrl = info.downloadUrl;
        downloadInfo.packageName = info.packageName;
        downloadInfo.size = info.size;

        downloadInfo.currentPos = 0;
        downloadInfo.currentState = DownloadManager.STATE_UNDO;//默认状态 未下载
        downloadInfo.path = downloadInfo.getFilePath();

        return downloadInfo;
    }

    /**
     * 获取文件下载路径
     */
    public String getFilePath() {
        StringBuffer sb = new StringBuffer();
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        sb.append(sdcard);
//        sb.append("/");
        sb.append(File.separator);//等于“/”
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DOWNLOAD);

        if (createDir(sb.toString())) { //文件夹存在 或 创建完成
            return sb.toString() + File.separator + name + ".apk";//返回文件的路径
        }

        return null;
    }

    private boolean createDir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {  //不存在，或者不是一个文件夹
            return dirFile.mkdirs();
        }
        return true;//文件夹存在
    }
}
