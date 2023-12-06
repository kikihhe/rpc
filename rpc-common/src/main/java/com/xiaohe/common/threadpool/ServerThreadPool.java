package com.xiaohe.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : 小何
 * @Description : 服务提供者用于执行任务的线程池
 * @date : 2023-12-03 22:29
 */
public class ServerThreadPool {
    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                16,
                16,
                600L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(65536)
        );
    }

    public static void submit(Runnable runnable) {
        threadPoolExecutor.submit(runnable);
    }
    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
