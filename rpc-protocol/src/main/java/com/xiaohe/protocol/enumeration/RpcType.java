package com.xiaohe.protocol.enumeration;

/**
 * @author : 小何
 * @Description : rpc 消息的类型，如请求、响应、心跳
 * @date : 2023-12-03 16:54
 */
public enum RpcType {
    /**
     * 请求
     */
    REQUEST(1),

    /**
     * 响应
     */
    RESPONSE(2),

    /**
     * 心跳
     */
    HEARTBEAT(3)

    ;
    private final int type;

    RpcType(int code) {
        this.type = code;
    }

    public int getType() {
        return type;
    }

    /**
     * 根据类型返回 RpcType
     * @param type
     * @return
     */
    public static RpcType findByType(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;
    }
}
