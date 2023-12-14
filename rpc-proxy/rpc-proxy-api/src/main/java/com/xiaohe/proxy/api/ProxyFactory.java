package com.xiaohe.proxy.api;

import com.xiaohe.proxy.api.config.ProxyConfig;
import com.xiaohe.spi.annotation.SPI;

/**
 * @author : 小何
 * @Description : 代理工厂
 * @date : 2023-12-08 15:14
 */
@SPI
public interface ProxyFactory {
    /**
     * 获取代理对象
     * @return
     * @param <T>
     */
    <T> T getProxy(Class<T> clazz);

    /**
     * 默认初始化方法
     * @param proxyConfig
     * @param <T>
     */
    default <T> void init(ProxyConfig<T> proxyConfig) {

    }
}
