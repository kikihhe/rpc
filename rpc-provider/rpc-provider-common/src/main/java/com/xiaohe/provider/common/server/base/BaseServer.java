package com.xiaohe.provider.common.server.base;

import com.xiaohe.codec.RpcDecoder;
import com.xiaohe.codec.RpcEncoder;
import com.xiaohe.provider.common.handler.RpcProviderHandler;
import com.xiaohe.provider.common.server.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-05 23:43
 */
public class BaseServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    protected String host = "127.0.0.1";

    protected int port = 27110;

    /**
     * 存储实体类关系
     */
    protected Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 使用的代理类型，例如 jdk、cglib、javaassis
     */
    private String reflectType;
    @Override
    public void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(reflectType, handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("Server started on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("RPC Server start error.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}