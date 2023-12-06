package com.xiaohe.protocol.header;

import com.xiaohe.common.id.IDFactory;
import com.xiaohe.constants.RpcConstants;
import com.xiaohe.protocol.enumeration.RpcType;

public class RpcHeaderFactory {

    public static RpcHeader getRequestHeader(String serializationType){
        RpcHeader header = new RpcHeader();
        // 使用 AtomicLong 生成消息id
        long requestId = IDFactory.getId();

        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        // 默认消息类型为 请求
        header.setMsgType((byte) RpcType.REQUEST.getType());
        // 默认失败
        header.setStatus((byte) 0x1);
        // 需要指定序列化类型
        header.setSerializationType(serializationType);
        return header;
    }
}