package com.xiaohe.protocol.enumeration;

/**
 * @author : 小何
 * @Description : rpc服务状态
 * @date : 2023-12-03 16:52
 */
public enum RpcStatus {
    SUCCESS(0),
    FAIL(1)
    ;
    private final int code;

    RpcStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
