package com.xiaohe.proxy.api;

import com.xiaohe.proxy.api.config.ProxyConfig;
import com.xiaohe.proxy.api.object.ObjectProxy;

/**
 * @author : 小何
 * @Description : 基础的代理工厂
 * @date : 2023-12-08 15:16
 */
public abstract class BaseProxyFactory<T> implements ProxyFactory {

    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        objectProxy = new ObjectProxy (
                proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getSerializationType(),
                proxyConfig.getTimeout(),
                proxyConfig.getRegistryService(),
                proxyConfig.getConsumer(),
                proxyConfig.getAsync(),
                proxyConfig.getOneway()
        );
    }
}
