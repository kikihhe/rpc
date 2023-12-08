package com.xiaohe.proxy.api.callback;

/**
 * @author : 小何
 * @Description : 异步调用模式中的回调
 * @date : 2023-12-07 22:44
 */
public interface AsyncRPCCallback {
    /**
     * 成功时执行的回调
     * @param result
     */
    void onSuccess(Object result);

    /**
     * 失败时执行的回到
     * @param e
     */
    void onException(Exception e);
}
