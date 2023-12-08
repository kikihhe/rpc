package com.xiaohe.test.scanner.provider;

import com.xiaohe.annotation.RpcService;
import com.xiaohe.test.scanner.service.DemoService;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.xiaohe.test.scanner.service.DemoService", version = "1.0.0", group = "xiaohe")
public class ProviderDemoServiceImpl implements DemoService {

    @Override
    public String hello(String name) {
        return "nihao";
    }
}
