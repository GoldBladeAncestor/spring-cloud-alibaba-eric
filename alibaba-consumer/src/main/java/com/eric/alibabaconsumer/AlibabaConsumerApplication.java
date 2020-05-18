package com.eric.alibabaconsumer;

import com.eric.alibabaconsumer.util.ApplicationContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaConsumerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AlibabaConsumerApplication.class, args);
        ApplicationContextUtils.setContext(applicationContext);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){ return new RestTemplate();}
}
