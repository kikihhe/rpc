package com.xiaohe.consumer.common.context;


import com.xiaohe.proxy.api.future.RpcFuture;

/**
 * @author : 小何
 * @Description : 消费者端的上下文，可以存储异步请求类型的响应
 * @date : 2023-12-07 21:33
 */
public class RpcContext {
    public RpcContext() {
    }

    private static final RpcContext AGENT = new RpcContext();

    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static RpcContext getContext() {
        return AGENT;
    }
    public void setRpcFuture(RpcFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }
    public RpcFuture getRPCFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    public void removeRPCFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
