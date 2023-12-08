package com.xiaohe.proxy.api.async;

import com.xiaohe.proxy.api.future.RpcFuture;

/**
 * @author : 小何
 * @Description : 异步调用
 * @date : 2023-12-08 14:29
 */
public interface IAsyncObjectProxy {
    /**
     * 根据方法名异步调用
     * @param funcName
     * @param args
     * @return
     */
    RpcFuture call(String funcName, Object... args);
}
