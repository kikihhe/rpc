package com.xiaohe.consumer;

import com.xiaohe.common.exception.RegistryException;
import com.xiaohe.consumer.common.RpcConsumer;
import com.xiaohe.proxy.api.ProxyFactory;
import com.xiaohe.proxy.api.async.IAsyncObjectProxy;
import com.xiaohe.proxy.api.config.ProxyConfig;
import com.xiaohe.proxy.api.object.ObjectProxy;
import com.xiaohe.proxy.jdk.JdkProxyFactory;
import com.xiaohe.registry.api.RegistryService;
import com.xiaohe.registry.api.config.RegistryConfig;
import com.xiaohe.registry.zookeeper.ZookeeperRegistryService;
import com.xiaohe.spi.factory.ExtensionFactory;
import com.xiaohe.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 09:21
 */
public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;


    /**
     * 注册服务
     */
    private RegistryService registryService;

    /**
     * 使用的动态代理
     */
    private String proxy;

    public RpcClient(String registryAddress, String registryType, String proxy, String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.proxy = proxy;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = this.getRegistryService(registryAddress, registryType);
    }
    public <T> T create(Class<T> interfaceClass) {
        // 使用SPI机制获取动态代理
        ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
        proxyFactory.init(new ProxyConfig<>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway));

        return proxyFactory.getProxy(interfaceClass);
    }
    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway);
    }
    public void shutdown() {
        RpcConsumer.getInstance().close();
    }

    private RegistryService getRegistryService(String registryAddress, String registryType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }
}
