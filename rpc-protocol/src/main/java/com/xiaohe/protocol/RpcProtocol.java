package com.xiaohe.protocol;

import com.xiaohe.protocol.header.RpcHeader;

import java.io.Serializable;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 17:11
 */
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 292789485166173277L;

    /**
     * 头
     */
    private RpcHeader header;

    /**
     * 体
     * 可能是 RpcRequest、RpcResponse
     */
    private T body;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
