package com.xiaohe.provider.common.handler;

import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.common.threadpool.ServerThreadPool;
import com.xiaohe.constants.RpcConstants;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.enumeration.RpcStatus;
import com.xiaohe.protocol.enumeration.RpcType;
import com.xiaohe.protocol.header.RpcHeader;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-06 14:13
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    private final String reflectType;

    public RpcProviderHandler( String reflectType, Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        this.reflectType = reflectType;
    }

    /**
     * 接受的信息如果处理
     * @param channelHandlerContext
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> request) throws Exception {
        ServerThreadPool.submit(() -> {
            logger.info("收到来自调用者的消息 : {}", request);
            RpcHeader header = request.getHeader();
            // Header再利用
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            RpcRequest requestBody = request.getBody();
            logger.debug("Receive request id : {}", header.getRequestId());
            RpcProtocol<RpcResponse> response = new RpcProtocol<>();
            RpcResponse responseBody = new RpcResponse();
            try {
                // 执行服务提供者的具体服务
                Object result = handle(requestBody);
                // 执行结束后设置到响应中
                responseBody.setResult(result);
                responseBody.setAsync(requestBody.getAsync());
                responseBody.setOneway(requestBody.getOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            } catch (Throwable t) {
                responseBody.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("Rpc Server handler request error");
            }
            response.setHeader(header);
            response.setBody(responseBody);
            channelHandlerContext.writeAndFlush(response).addListener(channelFuture -> {
                logger.debug("Send response for request : {}", header.getRequestId());
            });
        });
    }

    private Object handle(RpcRequest requestBody) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String serviceKey = RpcServiceHelper.buildServiceKey(requestBody.getClassName(), requestBody.getVersion(), requestBody.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist : %s",serviceKey));
        }
        // 被调用的类的 class
        Class<?> serviceClass = serviceBean.getClass();
        // 需要调用的方法
        String methodName = requestBody.getMethodName();
        // 方法中参数的类型
        Class<?>[] parameterTypes = requestBody.getParameterTypes();
        // 方法需要的参数
        Object[] parameters = requestBody.getParameters();
        // 打印一下将要执行的服务的信息 : 类名、方法名、参数
        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                logger.debug("{}", parameterTypes[i]);
            }
        }
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                logger.debug("{}", parameters[i]);
            }
        }


        Object result = invoke(serviceBean, serviceClass, methodName, parameterTypes, parameters);
        return result;
    }

    /**
     * 挑选代理方式，执行
     * @param serviceBean
     * @param serviceClass
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    private Object invoke(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch(reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK :
                return invokeJDKMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            case RpcConstants.REFLECT_TYPE_CGLIB:
                return invokeCglibMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            default:
                throw new IllegalArgumentException("not support reflect type");
        }
    }

    /**
     * 使用Cglib代理
     */
    private Object invokeCglibMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException {
        logger.info("use cglib reflect type invoke method");
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    /**
     * 使用jdk动态代理
     */
    private Object invokeJDKMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.info("use jdk reflect type invoke method...");
        Method method = serviceClass.getMethod(methodName);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
