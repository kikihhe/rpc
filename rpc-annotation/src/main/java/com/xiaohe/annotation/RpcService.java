package com.xiaohe.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 小何
 * @Description : 服务提供者 注解
 * @date : 2023-12-03 16:21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 该接口的class对象
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口名称
     * @return
     */
    String interfaceClassName() default "";

    /**
     * 版本号
     * @return
     */
    String version() default "1.0.0";

    /**
     * 服务分组，默认为空
     * @return
     */
    String group() default "";



}
