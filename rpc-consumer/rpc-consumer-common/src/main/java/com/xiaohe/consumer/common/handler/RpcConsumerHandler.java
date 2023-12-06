package com.xiaohe.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.header.RpcHeader;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-06 15:12
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);
    /**
     * 通信的channel，拿到channel可以发送数据
     */
    private volatile Channel channel;
    /**
     * 与此消费者通信的注册中心的地址
     */
    private SocketAddress remotePeer;

    /**
     * 请求id与请求结果的映射，是实现同步的关键
     */
    private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            return;
        }
        logger.info("消费者收到消息: {}", JSONObject.toJSON(protocol));
        long requestId = protocol.getHeader().getRequestId();
        pendingResponse.put(requestId, protocol);
    }

    /**
     * 服务消费者向服务提供者发送消息
     * @param protocol
     */
    public Object sendRequest(RpcProtocol<RpcRequest> protocol) {
        logger.info("服务消费者发送数据 : {}", JSONObject.toJSON(protocol));
        channel.writeAndFlush(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        while (true) {
            RpcProtocol<RpcResponse> responseRpcProtocol = pendingResponse.remove(requestId);
            if (responseRpcProtocol != null) {
                return responseRpcProtocol.getBody().getResult();
            }
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }
}
