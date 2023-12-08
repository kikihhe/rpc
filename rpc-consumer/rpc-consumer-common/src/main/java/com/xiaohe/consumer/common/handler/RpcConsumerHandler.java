package com.xiaohe.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.xiaohe.consumer.common.context.RpcContext;

import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.protocol.response.RpcResponse;
import com.xiaohe.proxy.api.future.RpcFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<Long, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

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
        RpcFuture rpcFuture = pendingRPC.remove(requestId);
        // 如果 rpcFuture 不为空说明这是第一次接收到 request id 对应请求的响应，将其标志为完成状态。
        if (rpcFuture != null) {
            rpcFuture.done(protocol);
        }
    }

    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async,  boolean oneway) {
        logger.info("服务消费者发送数据 : {}", JSONObject.toJSON(protocol));
        return oneway ? this.sendRequestOneway(protocol) : async ? this.sendRequestAsync(protocol) : this.sendRequestSync(protocol);
    }

    /**
     * 异步方式 : 服务消费者向服务提供者发送消息
     * @param protocol
     * @return
     */
    public RpcFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(rpcFuture);
        // 将异步请求放入context，到后面可以根据请求找到响应
        RpcContext.getContext().setRpcFuture(rpcFuture);
        // 返回空，让调用者拿不到这个 RpcFuture 只能根据 requestId 从 Context 中取 RpcFuture 然后 get response
        return null;
    }

    /**
     * 同步方式 : 服务消费者向服务提供者发送消息
     * @param protocol
     */
    public RpcFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
        // 根据请求数据封装RPCFuture，将 requestId-RPCFuture 的键值对放入Map
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    /**
     * 单端方式 : 服务消费者向服务提供者发送消息
     * @param protocol
     * @return
     */
    public RpcFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }

    /**
     * 根据请求生成对应的RPCFuture
     * @param protocol
     * @return
     */
    private RpcFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
        RpcFuture rpcFuture = new RpcFuture(protocol);
        long requestId = protocol.getHeader().getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
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
