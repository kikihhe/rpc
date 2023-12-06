package com.xiaohe.consumer.common;

import com.xiaohe.consumer.common.handler.RpcConsumerHandler;
import com.xiaohe.consumer.common.initializer.RpcConsumerInitializer;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.request.RpcRequest;
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
public class RpcConsumer {
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
      * @param protocol
     */
    public void sendRequest(RpcProtocol<RpcRequest> protocol) throws InterruptedException {
        // TODO 暂时写死，后续引入注册中心时，从注册中心获取
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        // 组装key = ${serviceAddress}-${port}, 从Map中找handler
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);
        // 看看有没有处理器，如果没有就创建一个添加到Map中
        // 如果有但是死亡，关闭后重新创建
        if (handler == null) {
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        } else if (!handler.getChannel().isActive()) {
            handler.close();
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }
        handler.sendRequest(protocol);
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
        eventLoopGroup.shutdownGracefully();
    }
}
