package com.xiaohe.test.provider.service.impl;

import com.xiaohe.annotation.RpcService;
import com.xiaohe.test.api.DemoService;
import org.apache.log4j.lf5.LF5Appender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 11:00
 */
@RpcService(
        interfaceClass = DemoService.class,
        interfaceClassName = "com.xiaohe.test.api.DemoService",
        version = "1.0.0",
        group = "xiaohe"
)
public class DemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
    @Override
    public String hello(String name) {
        logger.info("调用hello方法，入参:" + name);
        return "hello, " + name;
    }
}
