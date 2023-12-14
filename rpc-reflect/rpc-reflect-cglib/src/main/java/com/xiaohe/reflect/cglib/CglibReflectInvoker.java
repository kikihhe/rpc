package com.xiaohe.reflect.cglib;

import com.xiaohe.spi.annotation.SPIClass;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import com.xiaohe.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-14 19:50
 */
@SPIClass
public class CglibReflectInvoker implements ReflectInvoker {
    private final Logger logger = LoggerFactory.getLogger(CglibReflectInvoker.class);
    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        logger.info("use cglib reflect type invoke method...");
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
