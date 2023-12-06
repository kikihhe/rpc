package com.xiaohe.consumer.common.initializer;

import com.xiaohe.codec.RpcDecoder;
import com.xiaohe.codec.RpcEncoder;
import com.xiaohe.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author : 小何
 * @Description : 处理器
 * @date : 2023-12-06 15:11
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new RpcDecoder())
                .addLast(new RpcEncoder())
                .addLast(new RpcConsumerHandler());
    }
}
