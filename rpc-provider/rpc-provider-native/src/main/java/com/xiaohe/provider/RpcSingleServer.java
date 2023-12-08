package com.xiaohe.provider;

import com.xiaohe.provider.common.scanner.RpcServiceScanner;
import com.xiaohe.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 10:51
 */
public class RpcSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String registryAddress, String registryType, String scanPackage, String reflectType) {
        super(serverAddress, registryAddress, registryType, reflectType);
        try {
            // 启动时将所有服务提供者扫描出来放入Map
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
    }
}
