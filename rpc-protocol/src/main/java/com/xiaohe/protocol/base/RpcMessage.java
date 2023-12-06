package com.xiaohe.protocol.base;

import java.io.Serializable;

/**
 * @author : 小何
 * @Description : 消息基础类
 * @date : 2023-12-03 16:50
 */
public class RpcMessage implements Serializable {
    private boolean oneway;

    private boolean async;

    public boolean getOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean getAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
