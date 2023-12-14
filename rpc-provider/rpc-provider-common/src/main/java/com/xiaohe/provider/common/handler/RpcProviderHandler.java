package com.xiaohe.provider.common.handler;

import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.common.threadpool.ServerThreadPool;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.enumeration.RpcStatus;
import com.xiaohe.protocol.enumeration.RpcType;
import com.xiaohe.protocol.header.RpcHeader;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.protocol.response.RpcResponse;
import com.xiaohe.spi.loader.ExtensionLoader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.xiaohe.reflect.api.ReflectInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-06 14:13
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    private final ReflectInvoker reflectInvoker;

    public RpcProviderHandler( String reflectType, Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
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

    private Object handle(RpcRequest requestBody) throws Throwable {
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

        Object result = reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
        return result;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
