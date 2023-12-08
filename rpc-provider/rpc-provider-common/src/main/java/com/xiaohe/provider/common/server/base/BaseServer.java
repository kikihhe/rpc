package com.xiaohe.provider.common.server.base;

import com.xiaohe.codec.RpcDecoder;
import com.xiaohe.codec.RpcEncoder;
import com.xiaohe.provider.common.handler.RpcProviderHandler;
import com.xiaohe.provider.common.server.api.Server;
import com.xiaohe.registry.api.RegistryService;
import com.xiaohe.registry.api.config.RegistryConfig;
import com.xiaohe.registry.zookeeper.ZookeeperRegistryService;
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
import org.springframework.util.StringUtils;

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

    /**
     * 注册服务
     */
    protected RegistryService registryService;

    /**
     * 构造方法
     * @param serverAddress 本机的server地址
     * @param registryAddress 注册中心地址
     * @param registryType 注册中心类型
     * @param reflectType
     */
    public BaseServer(String serverAddress, String registryAddress, String registryType, String reflectType) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
        this.reflectType = reflectType;
        this.registryService = getRegistryService(registryAddress, registryType);
    }
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

    /**
     * 获取注册服务
     * @param registryAddress 地址
     * @param registryType 注册中心类型
     * @return
     */
    private RegistryService getRegistryService(String registryAddress, String registryType) {
        // TODO 现在只支持zookeeper，后续扩展Java SPI机制
        RegistryService registryService = null;
        try {
            registryService = new ZookeeperRegistryService();
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RPC Server init error");
        }
        return registryService;
    }
}
