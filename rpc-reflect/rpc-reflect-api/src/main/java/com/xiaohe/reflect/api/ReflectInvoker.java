package com.xiaohe.reflect.api;

import com.xiaohe.spi.annotation.SPI;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-14 19:45
 */
@SPI
public interface ReflectInvoker {
    /**
     * 调用真实方法的接口
     * @param serviceBean
     * @param serviceClass
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws Throwable
     */
    Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable;
}
