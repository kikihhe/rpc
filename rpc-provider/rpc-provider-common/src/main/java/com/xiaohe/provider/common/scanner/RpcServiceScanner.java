package com.xiaohe.provider.common.scanner;


import com.xiaohe.annotation.RpcService;
import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.common.scanner.ClassScanner;
import com.xiaohe.protocol.meta.ServiceMeta;
import com.xiaohe.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String host, int port, String scanPackage, RegistryService registryService) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
           return handlerMap;
        }
        classNameList.stream().forEach(className -> {
            try {
                // 根据类名拿到class
                Class<?> clazz = Class.forName(className);
                // class上是否有 RpcService 注解
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    String serviceName = getServiceName(rpcService);
                    String version = rpcService.version();
                    String group = rpcService.group();
                    ServiceMeta serviceMeta = new ServiceMeta(serviceName, version, group, host, port);
                    // 将这个服务注册到Zookeeper上
                    registryService.register(serviceMeta);
                    String key = RpcServiceHelper.buildServiceKey(serviceName, version, group);
                    // 将这个bean放入Map中
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
