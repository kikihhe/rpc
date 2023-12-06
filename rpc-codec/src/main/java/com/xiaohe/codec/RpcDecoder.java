package com.xiaohe.codec;

import com.xiaohe.common.util.SerializationUtil;
import com.xiaohe.constants.RpcConstants;
import com.xiaohe.protocol.RpcProtocol;
import com.xiaohe.protocol.enumeration.RpcType;
import com.xiaohe.protocol.header.RpcHeader;
import com.xiaohe.protocol.request.RpcRequest;
import com.xiaohe.protocol.response.RpcResponse;
import com.xiaohe.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-04 22:52
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 如果消息整体长度连头部都没有，这次消息留着跟下次一起解析。
        if (byteBuf.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 记录现在读到哪里了，方便回溯
        byteBuf.markReaderIndex();
        // 读取 魔数、消息类型、状态、消息ID
        short magic = byteBuf.readShort();
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();

        // 再读16个字节，表示消息的序列化方式
        ByteBuf serializationTypeByteBuf = byteBuf.readBytes(SerializationUtil.MAX_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtil.subString(serializationTypeByteBuf.toString(StandardCharsets.UTF_8));

        // 读出消息体长度
        int dataLength = byteBuf.readInt();
        // 如果剩余的字节数与消息中记录的数据长度对不上，说明数据包拆包了，回到刚才做标记的下标，下次来消息重新读上面的内容
        if (dataLength > byteBuf.readableBytes()) {
            byteBuf.resetReaderIndex();
            return;
        }
        // 将数据读出来
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        RpcType msgTypeEnum = RpcType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        // 开始组装信息
        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);
        Serialization jdkSerialization = getJDKSerialization();
        // 根据消息的类型做出不同的处理
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = jdkSerialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    list.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = jdkSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    list.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO 心跳检测没做
                break;
            default:
                break;
        }
    }
}
