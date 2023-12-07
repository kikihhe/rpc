package com.xiaohe.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : 小何
 * @Description : 服务消费者的线程池
 * @date : 2023-12-07 22:49
 */
public class ClientThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;
    static {
        threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    }
    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
