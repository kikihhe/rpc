package com.xiaohe.test.consumer;

import com.xiaohe.consumer.RpcClient;
import com.xiaohe.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 11:26
 */
public class RpcConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);
    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("1.0.0", "xiaohe", "jdk", 3000, false, false);
        // create的时候就把请求发出去了
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("小明");
        logger.info("返回的结果: " + result);
        rpcClient.shutdown();
    }
}
