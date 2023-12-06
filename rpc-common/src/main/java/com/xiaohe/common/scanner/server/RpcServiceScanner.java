package com.xiaohe.common.scanner.server;


import com.xiaohe.annotation.RpcService;
import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 小何
 * @Description : 扫描服务提供者
 * @date : 2023-12-03 23:05
 */
public class RpcServiceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);

    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
           return handlerMap;
        }
        classNameList.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    // TODO 后续会向注册中心注册服务的元数据，同时向 handlerMap 中记录标注了 RpcService 注解的类实例
                    String serviceName = getServiceName(rpcService);
                    String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }

    /**
     * 从注解中获取服务名称
     * @param rpcService
     * @return
     */
    private static String getServiceName(RpcService rpcService) {
        // 优先使用 class
        Class<?> clazz = rpcService.interfaceClass();
        if(clazz == void.class) {
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if(serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;

    }

}
