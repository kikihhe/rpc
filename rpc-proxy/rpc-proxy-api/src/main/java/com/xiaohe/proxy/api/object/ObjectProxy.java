package com.xiaohe.proxy.api.object;

import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.header.RpcHeaderFactory;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.proxy.api.async.IAsyncObjectProxy;
import com.xiaohe.proxy.api.consumer.Consumer;
import com.xiaohe.proxy.api.future.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 08:40
 */
public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {
    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);

    private Class<T> clazz;
    private String serviceVersion;
    private String serviceGroup;
    private long timeout = 15000;

    /**
     * 消费者
     */
    private Consumer consumer;
    /**
     * 序列化方式
     */
    private String serializationType;

    private boolean async;

    private boolean oneway;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType, long timeout, Consumer consumer, boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler" + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<RpcRequest>();

        requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType));

        RpcRequest request = new RpcRequest();
        request.setVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setGroup(this.serviceGroup);
        request.setParameters(args);
        request.setAsync(async);
        request.setOneway(oneway);
        requestRpcProtocol.setBody(request);

        // Debug
        logger.debug(method.getDeclaringClass().getName());
        logger.debug(method.getName());

        if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                logger.debug(method.getParameterTypes()[i].getName());
            }
        }
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                logger.debug(args[i].toString());
            }
        }

        RpcFuture rpcFuture = this.consumer.sendRequest(requestRpcProtocol);
        // 一般情况下 future 不为空，内部的 response 可能为空
        // 如果有限等待，最多执行5s，
        // 如果 timeout <= 0 则无限等待至结果返回
        // 不管怎样都要get之后才能返回，即必须有response才能返回，也就是同步方式
        return rpcFuture == null ? null : timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get();
    }

    @Override
    public RpcFuture call(String funcName, Object... args) {
        RpcProtocol<RpcRequest> request = createRequest(this.clazz.getName(), funcName, args);
        RpcFuture rpcFuture = null;
        try {
            rpcFuture = this.consumer.sendRequest(request);
        } catch (Exception e) {
            logger.error("async all throws exception:{}", e);
        }
        // 直接把future返回，里面的response不管。
        return rpcFuture;
    }


    /**
     * 根据方法名创建protocol
     * @param className
     * @param methodName
     * @param args
     * @return
     */
    private RpcProtocol<RpcRequest> createRequest(String className, String methodName, Object[] args) {
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType));
        RpcRequest request = new RpcRequest();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        request.setVersion(this.serviceVersion);
        request.setGroup(this.serviceGroup);

        // 设置参数的类型
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        requestRpcProtocol.setBody(request);

        // 打印一下日志
        logger.debug(className);
        logger.debug(methodName);
        for (int i = 0; i < parameterTypes.length; i++) {
            logger.debug(parameterTypes[i].getName());
        }
        for (int i = 0 ; i < args.length; i++) {
            logger.debug(args[i].toString());
        }
        return requestRpcProtocol;
    }

    /**
     * 获取参数的class类型
     * @param obj
     * @return
     */
    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        // 如果是基本数据类型，返回它们的包装类
        switch (typeName) {
            case "java.lang.Integer" :
                return Integer.TYPE;
            case "java.lang.Long" :
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double" :
                return Double.TYPE;
            case "java.lang.Character" :
                return Character.TYPE;
            case "java.lang.Boolean" :
                return Boolean.TYPE;
            case "java.lang.Short" :
                return Short.TYPE;
            case "java.lang.Byte" :
                return Byte.TYPE;
        }
        // 如果不是基本数据类型，直接返回
        return classType;
    }
}
