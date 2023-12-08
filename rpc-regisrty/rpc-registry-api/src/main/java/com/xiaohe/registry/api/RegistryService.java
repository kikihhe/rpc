package com.xiaohe.registry.api;

import com.xiaohe.protocol.meta.ServiceMeta;
import com.xiaohe.registry.api.config.RegistryConfig;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 15:39
 */
public interface RegistryService {
    /**
     * 服务注册
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 取消服务注册
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode值
     * @return
     * @throws Exception
     */
    ServiceMeta discover(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 服务销毁
     * @throws Exception
     */
    void destroy() throws Exception;

    /**
     * 默认初始化方法
     * @param registryConfig
     * @throws Exception
     */
    default void init(RegistryConfig registryConfig) throws Exception {

    }

}
