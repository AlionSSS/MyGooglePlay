package com.skey.mygoogleplay.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程的管理器
 *
 * @author ALion on 2016/10/25 16:41
 */

public class ThreadManager {

    private static ThreadPool mThreadPool;

    public static ThreadPool getInstance() {
        if (mThreadPool == null) {
            synchronized (ThreadManager.class) {
                if (mThreadPool == null) {
                    int cpuCount = Runtime.getRuntime().availableProcessors();//cpu核心数
                    System.out.println("cpuCount=" + cpuCount);
//                    int threadCount = cpuCount * 2 + 1;//线程个数
                    int threadCount = 10;//线程个数
                    mThreadPool = new ThreadPool(threadCount, threadCount, 1L);
                }
            }
        }
        return mThreadPool;
    }

    //线程池
    public static class ThreadPool {

        private int corePoolSize;//arg0:核心线程数
        private int maximumPoolSize;//arg1:最大线程数
        private long keepAliveTime;//qrg2:休息时间
        private ThreadPoolExecutor executor;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        //线程池几个参数的理解:
        //比如去火车站买票, 有10个售票窗口, 但只有5个窗口对外开放. 那么对外开放的5个窗口称为核心线程数（corePoolSize）,
        //而最大线程数是10个窗口（maximumPoolSize）.
        //如果5个窗口都被占用, 那么后来的人就必须在后面排队（new LinkedBlockingQueue）, 但后来售票厅人越来越多, 已经人满为患, 就类似于线程队列已满.
        //这时候火车站站长下令, 把剩下的5个窗口也打开, 也就是目前已经有10个窗口同时运行. 后来又来了一批人,
        //10个窗口也处理不过来了, 而且售票厅人已经满了, 这时候站长就下令封锁入口,不允许其他人再进来, 这就是线程异常处理策略（AbortPolicy）.
        //而线程存活时间指的是, 允许售票员休息的最长时间（keepAliveTime）, 以此限制售票员偷懒的行为.
        public void execute(Runnable r) {
            //arg3:keepAliveTime的单位，此处是秒, arg4:线程队列, arg5:线程生产工厂, arg6:线程异常处理策略
            if (executor == null) {
                executor = new ThreadPoolExecutor(
                        corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                );
            }
            executor.execute(r);//执行一个Runnable对象
        }

        /**
         * 取消任务
         */
        public void cancel(Runnable r) {
            if (executor != null)
                executor.getQueue().remove(r);//从线程队列中移除对象
        }
    }
}
