package com.xiaohe.proxy.api.consumer;

import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.proxy.api.future.RpcFuture;
import com.xiaohe.registry.api.RegistryService;

/**
 * @author : 小何
 * @Description : 服务消费者
 * @date : 2023-12-08 08:38
 */
public interface Consumer {
    /**
     * 发送请求
     * @param protocol
     * @return
     * @throws Exception
     */
    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;
}
