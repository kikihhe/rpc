package com.xiaohe.test.registry.zookeeper;

import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.protocol.meta.ServiceMeta;
import com.xiaohe.registry.api.RegistryService;
import com.xiaohe.registry.api.config.RegistryConfig;
import com.xiaohe.registry.zookeeper.ZookeeperRegistryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 20:07
 */
public class ZookeeperRegistryTest {
    // 注册工具
    private RegistryService registryService;
    // 注册元数据
    private ServiceMeta serviceMeta;

    private String zookeeperAddress = "114.115.208.175:2181";
    private String registryType = "zookeeper";
    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig(zookeeperAddress, registryType);
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta("com.xiaohe.UserService", "1.0.0", "xiaohe", "127.0.0.1", 8080);
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnRegister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        String serviceName = serviceMeta.getServiceName();
        String serviceVersion = serviceMeta.getServiceVersion();
        String serviceGroup = serviceMeta.getServiceGroup();

        String key = RpcServiceHelper.buildServiceKey(serviceName, serviceVersion, serviceGroup);
        ServiceMeta discover = this.registryService.discover(key, "xiaohe".hashCode());
        System.out.println(discover);
    }

    @Test
    public void testDestroy() throws Exception {
        this.registryService.destroy();
    }
}
