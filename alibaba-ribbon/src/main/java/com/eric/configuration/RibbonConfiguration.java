package com.eric.configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * java代码实现细粒度配置
 * 注意是单独的包
 * 父子上下文重复扫猫问题
 */
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new ZoneAvoidanceRule();
    }
}