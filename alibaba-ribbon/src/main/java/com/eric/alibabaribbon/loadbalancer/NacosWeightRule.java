package com.eric.alibabaribbon.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基于Nacos 权重的负载均衡算法
 */
public class NacosWeightRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件，并初始化NacosWeightRule
    }

    @Override
    public Server choose(Object o) {
        try {
            // ribbon入口
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();

            //想要请求的微服务的名称
            String name = loadBalancer.getName();

            // 实现负载均衡算法
            // 拿到服务发现的相关api
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // nacos client自动通过基于权重的负载均衡算法，给我们一个示例
            Instance instance = namingService.selectOneHealthyInstance(name);

            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}