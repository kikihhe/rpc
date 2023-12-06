package com.xiaohe.codec;

import com.xiaohe.common.util.SerializationUtil;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.header.RpcHeader;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author : 小何
 * @Description : 编码器，将RpcProtocol通过序列化方式转换为二进制
 * @date : 2023-12-04 16:02
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        RpcHeader header = msg.getHeader();
        // 将头信息写入ByteBuf
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());

        String serializationType = header.getSerializationType();
        // TODO 序列化方式，扩展点
        Serialization jdkSerialization = getJDKSerialization();
        // 将序列化方式写入ByteBuf
        byteBuf.writeBytes(SerializationUtil.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
        // 开始内容的序列化
        byte[] data = jdkSerialization.serialize(msg.getBody());
        // 将内容长度写入ByteBuf
        byteBuf.writeInt(data.length);
        // 将消息体写入ByteBuf
        byteBuf.writeBytes(data);
    }
}


