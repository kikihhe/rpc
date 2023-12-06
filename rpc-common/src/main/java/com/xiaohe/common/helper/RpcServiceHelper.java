package com.xiaohe.common.helper;

/**
 * @author : 小何
 * @Description : RPC服务帮助类
 * @date : 2023-12-03 22:26
 */
public class RpcServiceHelper {
    /**
     * 拼接字符串，在 serviceName、serviceVersion、group 之间拼接 #
     * @param serviceName
     * @param serviceVersion
     * @param group
     * @return
     */
    public static String buildServiceKey(String serviceName, String serviceVersion, String group) {
        return String.join("#", serviceName, serviceVersion, group);
    }
}
