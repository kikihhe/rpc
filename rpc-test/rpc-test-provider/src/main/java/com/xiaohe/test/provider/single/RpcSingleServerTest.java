package com.xiaohe.test.provider.single;

import com.xiaohe.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author : 小何
 * @Description : 测试Java原生方式启动RPC
 * @date : 2023-12-08 11:03
 */
public class RpcSingleServerTest {
    @Test
    public void startRpcSingleServer() {
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "localhost:2181", "zookeeper","com.xiaohe.test.provider", "cglib");
        singleServer.startNettyServer();
    }
}
