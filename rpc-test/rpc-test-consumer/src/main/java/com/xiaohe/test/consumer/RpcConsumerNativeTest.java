package com.xiaohe.test.consumer;

import com.xiaohe.consumer.RpcClient;
import com.xiaohe.proxy.api.async.IAsyncObjectProxy;
import com.xiaohe.proxy.api.future.RpcFuture;
import com.xiaohe.test.api.DemoService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 11:26
 */
public class RpcConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);
    //    public static void main(String[] args) {
//        RpcClient rpcClient = new RpcClient("localhost:2181", "zookeeper", "1.0.0", "xiaohe", "jdk", 3000, false, false);
//        // create的时候就把请求发出去了
//        DemoService demoService = rpcClient.create(DemoService.class);
//        String result = demoService.hello("小明");
//        logger.info("返回的结果: " + result);
//        rpcClient.shutdown();
//    }
    private RpcClient rpcClient;

    @Before
    public void init() {
        rpcClient = new RpcClient("localhost:2181", "zookeeper", "1.0.0", "xiaohe", "jdk", 3000, false, false);
    }

    @Test
    public void testInterfaceRpc() {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("xiaohe");
        logger.info("返回的数据: " + result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy async = rpcClient.createAsync(DemoService.class);
        RpcFuture future = async.call("hello", "xiaohe");
        logger.info("返回的数据:" + future.get());
        rpcClient.shutdown();
    }
}
