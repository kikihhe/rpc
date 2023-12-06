package com.xiaohe.common.id;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 16:33
 */
public class IDFactory {
    private static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    public static Long getId() {
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
