package com.xiaohe.common.scanner.reference;

import com.xiaohe.annotation.RpcReference;
import com.xiaohe.common.scanner.ClassScanner;
import com.xiaohe.common.scanner.server.RpcServiceScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-05 00:02
 */
public class RpcReferenceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * 扫描指定包中使用了 @RpcReference 的变量
     * @param scanPackage
     * @return
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach(field -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null) {
                        // TODO 将RpcReference 注解标注的接口引用代理对象，放入全局缓存中。
                        logger.info("当前标注了@RpcReference注解的字段名称===>>> " + field.getName());
                        logger.info("@RpcReference注解上标注的属性信息如下：");
                        logger.info("version===>>> " + rpcReference.version());
                        logger.info("group===>>> " + rpcReference.group());
                        logger.info("registryType===>>> " + rpcReference.registryType());
                        logger.info("registryAddress===>>> " + rpcReference.registryAddress());
                    }
                });


            } catch (ClassNotFoundException e) {
                logger.error("scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }
}
