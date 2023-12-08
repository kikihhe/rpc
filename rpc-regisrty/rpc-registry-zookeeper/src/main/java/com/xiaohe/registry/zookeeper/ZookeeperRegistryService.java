package com.xiaohe.registry.zookeeper;

import com.xiaohe.common.helper.RpcServiceHelper;
import com.xiaohe.protocol.meta.ServiceMeta;
import com.xiaohe.registry.api.RegistryService;
import com.xiaohe.registry.api.config.RegistryConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 15:55
 */
public class ZookeeperRegistryService implements RegistryService {
    /**
     * 连接失败后，再次进行重试的时间间隔， ms
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRIES = 3;
    /**
     * zookeeper 根路径
     */
    public static final String ZK_BASE_PATH = "/xiaohe_rpc";

    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 初始化，启动zookeeper客户端
     * @param registryConfig
     * @throws Exception
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();

    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        // 将本机信息构建为一个实例
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        // 注册到 zookeeper
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discover(String serviceName, int invokerHashCode) throws Exception {
        // 根据serviceName拿到所有实例
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        // 负载均衡挑选一个
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
        // 不为空就返回元数据
        if (instance != null) {
            return instance.getPayload();
        }
        // 为空则返回null
        return null;
    }

    /**
     * 随机挑选一个
     * @param serviceInstances
     * @return
     */
    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances){
        if (serviceInstances == null || serviceInstances.isEmpty()){
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }
    @Override
    public void destroy() throws Exception {
        serviceDiscovery.close();
    }
}
