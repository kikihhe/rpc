package com.xiaohe.consumer.common.helper;

import com.xiaohe.consumer.common.handler.RpcConsumerHandler;
import com.xiaohe.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 22:56
 */
public class RpcConsumerHandlerHelper {
    private static Map<String, RpcConsumerHandler> rpcConsumerHandlerMap;
    static {
        rpcConsumerHandlerMap = new ConcurrentHashMap<>();
    }
    private static String getKey(ServiceMeta serviceMeta) {
        return serviceMeta.getServiceAddr().concat("_").concat(String.valueOf(serviceMeta.getServicePort()));
    }
    public static void put(ServiceMeta serviceMeta, RpcConsumerHandler handler) {
        rpcConsumerHandlerMap.put(getKey(serviceMeta), handler);
    }
    public static RpcConsumerHandler get(ServiceMeta serviceMeta) {
        return rpcConsumerHandlerMap.get(getKey(serviceMeta));
    }

    /**
     * 关闭所有处理器
     */
    public static void closeRpcClientHandler() {
        Collection<RpcConsumerHandler> rpcClientHandlers = rpcConsumerHandlerMap.values();
        rpcClientHandlers.forEach(RpcConsumerHandler::close);
    }
}
