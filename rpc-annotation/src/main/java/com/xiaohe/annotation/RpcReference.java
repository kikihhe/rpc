package com.xiaohe.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 16:24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcReference {
    /**
     * 版本号
     * @return
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型，如zookeeper、nacos、etcd
     * @return
     */
    String registryType() default "zookeeper";

    /**
     * 注册地址
     * @return
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡类型，默认基于ZK的一致性HASH
     * @return
     */
    String loadBalanceType() default "zkconsistenthash";

    /**
     * 默认的序列化类型，默认使用 protostuff
     * @return
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间，默认5s
     * @return
     */
    long timeout() default 5000;

    /**
     * 是否异步，默认false
     * @return
     */
    boolean async() default false;

    /**
     * 是否单向调用，默认false
     * @return
     */
    boolean oneway() default false;

    /**
     * 代理类型，默认jdk。可以有: jdk、javassist、cglib
     * @return
     */
    String proxy() default "jdk";

    /**
     * 服务分组
     * @return
     */
    String group() default "";
}
