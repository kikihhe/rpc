package com.xiaohe.registry.api.config;

import java.io.Serializable;

public class RegistryConfig implements Serializable {
    private static final long serialVersionUID = -7248658103788758893L;

    /**
     * zookeeper地址
     */
    private String registryAddr;

    /**
     * 注册类型
     * nacos zookeeper etcd...
     */
    private String registryType;


    public RegistryConfig(String registryAddr, String registryType) {
        this.registryAddr = registryAddr;
        this.registryType = registryType;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

}