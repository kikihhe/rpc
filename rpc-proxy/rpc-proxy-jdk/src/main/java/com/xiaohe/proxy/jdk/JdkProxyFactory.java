package com.xiaohe.proxy.jdk;

import com.xiaohe.proxy.api.BaseProxyFactory;
import com.xiaohe.proxy.api.ProxyFactory;
import com.xiaohe.proxy.api.consumer.Consumer;
import com.xiaohe.proxy.api.object.ObjectProxy;
import com.xiaohe.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 08:58
 */
@SPIClass
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    private static final Logger logger = LoggerFactory.getLogger(JdkProxyFactory.class);
    public <T> T getProxy(Class<T> clazz) {
        logger.info("基于 JDK动态代理");
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }


}
