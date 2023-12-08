package com.xiaohe.proxy.api.object;

import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.header.RpcHeaderFactory;
import com.xiaohe.protocol.request.RpcRequest;
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
public class ObjectProxy<T> implements InvocationHandler {
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
        // rpcFuture == null 的情况 : 此次发送为异步发送、单向发送，否则为rpcFuture!=null
        // 如果是同步等待，最多执行5s，直接get
        // 如果 timeout <= 0 则无限等待至结果返回
        return rpcFuture == null ? null : timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get();
    }
}
