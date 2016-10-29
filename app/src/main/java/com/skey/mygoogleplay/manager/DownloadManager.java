package com.skey.mygoogleplay.manager;

import android.content.Intent;
import android.net.Uri;

import com.skey.mygoogleplay.domain.AppInfo;
import com.skey.mygoogleplay.domain.DownloadInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.utils.IOUtils;
import com.skey.mygoogleplay.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 下载的管理器
 * -未下载 -等待下载 -正在下载 -暂停下载 -下载失败 -下载成功
 * DownloadManager: 被观察者，有责任通知所有观察者和进度发生变化
 *
 * @author ALion on 2016/10/25 20:33
 */
public class DownloadManager {

    public static final int STATE_UNDO = 0;//-未下载
    public static final int STATE_WAITING = 1;//-等待下载
    public static final int STATE_DOWNLOADING = 2;//-正在下载
    public static final int STATE_PAUSE = 3;//-暂停下载
    public static final int STATE_ERROR = 4;//-下载失败
    public static final int STATE_SUCCESS = 5;//-下载成功

    //饿汉模式
    private static DownloadManager mDM = new DownloadManager();

    private ArrayList<DownloadObserver> mObservers = new ArrayList<>();//4.观察者集合

    private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>();//下载信息的集合

    private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();//下载任务的集合

    private DownloadManager() {

    }

    public static DownloadManager getInstance() {
        return mDM;
    }

    /**
     * 2.注册观察者
     */
    public void registerObserver(DownloadObserver observer) {
        if (observer != null && !mObservers.contains(observer))
            mObservers.add(observer);
    }

    /**
     * 3.注销观察者
     */
    public void unRegisterObserver(DownloadObserver observer) {
        if (observer != null && mObservers.contains(observer))
            mObservers.remove(observer);
    }

    //5.通知下载状态发生变化
    public void notifyDownloadStateChanged(DownloadInfo info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadStateChanged(info);
        }
    }

    //6.通知下载进度发生变化
    public void notifyDownloadProgressChanged(DownloadInfo info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadProgressChanged(info);
        }
    }

    /**
     * 开始下载
     */
    public synchronized void download(AppInfo info) {
        //如果对象是第一次下载，需要创建一个新的DownloadInfo对象，从头下载
        //如果之前下载过，要接着下载，实现断点续传
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if (downloadInfo == null)
            downloadInfo = DownloadInfo.copy(info);//生成下载的对象

        downloadInfo.currentState = STATE_WAITING;//切换到-等待下载
        notifyDownloadStateChanged(downloadInfo);//通知所有观察者，状态改变
        System.out.println(downloadInfo.name + "等待下载");

        mDownloadInfoMap.put(downloadInfo.id, downloadInfo);//将下载对象信息放入集合中

        //下载任务，放入线程池中运行
        DownloadTask task = new DownloadTask(downloadInfo);
        ThreadManager.getInstance().execute(task);//开始下载
        mDownloadTaskMap.put(downloadInfo.id, task);//将下载任务放入集合中
    }

    private class DownloadTask implements Runnable {

        private DownloadInfo downloadInfo;

        DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            System.out.println(downloadInfo.name + "开始下载");

            downloadInfo.currentState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(downloadInfo);

            File file = new File(downloadInfo.path);
            HttpHelper.HttpResult httpResult;
            if (!file.exists() || file.length() != downloadInfo.currentPos || downloadInfo.currentPos == 0) {
                //不存在or文件不对，重头下载
                //删除无效文件
                file.delete();//文件如果不存在，也是可以删除的
                downloadInfo.currentPos = 0;//当前位置重置为0

                //从头开始下载
                httpResult = HttpHelper.download(
                        HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl);
            } else {
                //存在,断点续传
                //range表示请求服务器从文件的那个位置开始返回数据
                httpResult = HttpHelper.download(
                        HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl + "&range=" + file.length());
            }

            if (httpResult != null && httpResult.getInputStream() != null) {
                InputStream in = httpResult.getInputStream();
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file, true);//true表示在原有文件上追加
                    int len;
                    byte[] buffer = new byte[1024];
                    //只有状态是正在下载，才继续轮循，解决下载中途暂停的问题
                    while ((len = in.read(buffer)) != -1 && downloadInfo.currentState == STATE_DOWNLOADING) {
                        out.write(buffer, 0, len);
                        out.flush();//把剩余数据刷入本地，保证真正写入本地的len和downloadInfo.currentPos一样

                        //更新下载进度
                        downloadInfo.currentPos += len;
                        notifyDownloadProgressChanged(downloadInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(in);
                    IOUtils.close(out);
                }

                //文件下载结束
                if (file.length() == downloadInfo.size) {   //文件完整，下载成功
                    downloadInfo.currentState = STATE_SUCCESS;
                } else if (downloadInfo.currentState == STATE_PAUSE) {   //中途暂停

                } else {    //下载失败
                    file.delete();//删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    downloadInfo.currentPos = 0;
                }
                notifyDownloadStateChanged(downloadInfo);
            } else {
                //网络异常
                file.delete();//删除无效文件
                downloadInfo.currentState = STATE_ERROR;
                downloadInfo.currentPos = 0;
                notifyDownloadStateChanged(downloadInfo);
            }

            //从集合中移除
            mDownloadTaskMap.remove(downloadInfo.id);
        }
    }

    /**
     * 下载暂停
     */
    public synchronized void pause(AppInfo info) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);//取出下载对象信息
        if (downloadInfo != null) {
            if (downloadInfo.currentState == STATE_DOWNLOADING || downloadInfo.currentState == STATE_WAITING) {
                //切换状态
                downloadInfo.currentState = STATE_PAUSE;
                notifyDownloadStateChanged(downloadInfo);
                //取消该任务
                DownloadTask task = mDownloadTaskMap.get(downloadInfo.id);
                if (task != null)
                    ThreadManager.getInstance().cancel(task);//如果任务还没开始，正在队列中，那么可以通过此方法移除
                //如果任务已经开始，需要在run方法里面中断：while循环条件中加downloadInfo.currentState == STATE_DOWNLOADING
            }
        } else {
            System.out.println("downloadInfo====null");
        }
    }

    /**
     * 安装
     */
    public synchronized void install(AppInfo info) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if (downloadInfo != null) {
            //跳到系统安装页面
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path), "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    //1.声明观察者的接口
    public interface DownloadObserver {
        void onDownloadStateChanged(DownloadInfo info);//下载状态变化

        void onDownloadProgressChanged(DownloadInfo info);//下载进度变化
    }

    /**
     * 根据应用信息返回下载对象
     */
    public DownloadInfo getDownloadInfo(AppInfo info) {
        return mDownloadInfoMap.get(info.id);
    }
}
