package com.xiaohe.consumer.common;


import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.common.threadpool.ClientThreadPool;
import com.xiaohe.consumer.common.handler.RpcConsumerHandler;
import com.xiaohe.consumer.common.helper.RpcConsumerHandlerHelper;
import com.xiaohe.consumer.common.initializer.RpcConsumerInitializer;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.meta.ServiceMeta;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.proxy.api.consumer.Consumer;
import com.xiaohe.proxy.api.future.RpcFuture;
import com.xiaohe.registry.api.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-06 15:20
 */
public class RpcConsumer implements Consumer {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();
    private static volatile RpcConsumer instance;

    private RpcConsumer() {
        bootstrap = new io.netty.bootstrap.Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    public static RpcConsumer getInstance() {
        if (null == instance) {
            synchronized (RpcConsumer.class) {
                if (null == instance) {
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    /**
     * 发送数据
     * @param protocol 需要发送的数据
     * @param registryService 注册服务
     * @return
     * @throws Exception
     */
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        // 计算hashcode, 如果参数为空，使用serviceKey的hashCode，不为空就使用第一个参数的hashCode
        int invokerHashCode = (params == null || params.length == 0) ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = registryService.discover(serviceKey, invokerHashCode);
        if (serviceMeta == null) {
            return null;
        }
        RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
        if (handler == null) {
            handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        } else if (!handler.getChannel().isActive()) {
            handler.close();
            handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        }
        return handler.sendRequest(protocol, request.getAsync(), request.getOneway());
    }

    private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
                } else {
                    logger.info("connect rpc server {} on port {} failed.", serviceAddress, port);
                    logger.error(channelFuture.cause().toString());
                    eventLoopGroup.shutdownGracefully();
                }
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }

    public void close() {
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }
}
