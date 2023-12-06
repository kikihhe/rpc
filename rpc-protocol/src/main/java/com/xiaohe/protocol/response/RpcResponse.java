package com.xiaohe.protocol.response;

import com.xiaohe.protocol.base.RpcMessage;

/**
 * @author : 小何
 * @Description : rpc响应
 * @date : 2023-12-03 17:09
 */
public class RpcResponse extends RpcMessage {
    private static final long serialVersionUID = 425335064405584525L;

    private String error;

    private Object result;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
