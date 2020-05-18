package com.eric.alibabaribbon.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
* 同一集群优先调用
*/
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    private static final Logger log = LoggerFactory.getLogger(NacosSameClusterWeightedRule.class);

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            // 拿到配置文件中的集群名称
            String clusterName = nacosDiscoveryProperties.getClusterName();

            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            String name = loadBalancer.getName();
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // 找到指定服务的所有示例 A
            List<Instance> instances = namingService.selectInstances(name, true);
            // 过滤出相同集群的所有示例 B
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());
            // 如果B是空，就用A
            List<Instance> instancesToBeanChoose = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeanChoose = instances;
            } else {
                instancesToBeanChoose = sameClusterInstances;
            }
            // 基于权重的负载均衡算法返回示例
            Instance hostByRandomWeightExtend = ExtendBalancer.getHostByRandomWeightExtend(instancesToBeanChoose);

            return new NacosServer(hostByRandomWeightExtend);
        } catch (NacosException e) {
            log.error("发生异常了", e);
            return null;
        }
    }
}

class ExtendBalancer extends Balancer {
    public static Instance getHostByRandomWeightExtend(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}