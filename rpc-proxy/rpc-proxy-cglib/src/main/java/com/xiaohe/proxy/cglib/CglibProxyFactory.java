package com.xiaohe.proxy.cglib;

import com.xiaohe.proxy.api.BaseProxyFactory;
import com.xiaohe.proxy.api.ProxyFactory;
import com.xiaohe.proxy.api.config.ProxyConfig;
import com.xiaohe.spi.annotation.SPIClass;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-14 19:25
 */
@SPIClass
public class CglibProxyFactory extends BaseProxyFactory implements ProxyFactory {
    private static final Logger logger = LoggerFactory.getLogger(CglibProxyFactory.class);
    private final Enhancer enhancer = new Enhancer();
    @Override
    public <T> T getProxy(Class<T> clazz) {
        logger.info("基于 Cglib动态代理");
        enhancer.setInterfaces(new Class[]{clazz});
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return objectProxy.invoke(o, method, objects);
            }
        });
        return (T) enhancer.create();
    }
}
