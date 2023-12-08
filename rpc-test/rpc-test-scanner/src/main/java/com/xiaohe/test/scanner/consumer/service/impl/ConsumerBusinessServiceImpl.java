package com.xiaohe.test.scanner.consumer.service.impl;

import com.xiaohe.annotation.RpcReference;
import com.xiaohe.test.scanner.consumer.service.ConsumerBusinessService;
import com.xiaohe.test.scanner.service.DemoService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "xiaohe")
    private DemoService demoService;

}