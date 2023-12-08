package com.xiaohe.proxy.jdk;

import com.xiaohe.proxy.api.BaseProxyFactory;
import com.xiaohe.proxy.api.ProxyFactory;
import com.xiaohe.proxy.api.consumer.Consumer;
import com.xiaohe.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 08:58
 */
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }


}
