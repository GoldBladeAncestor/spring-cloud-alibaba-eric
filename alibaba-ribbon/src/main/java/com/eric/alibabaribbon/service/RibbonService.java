package com.eric.alibabaribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RibbonService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(
        fallbackMethod = "ribbonFallBack",
        commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"), // 开启熔断机制
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"), // 设置当请求失败的数量达到10个后，打开断路器，默认值为20
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // 设置打开断路器多久以后开始尝试恢复，默认为5s
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),  // 设置出错百分比阈值，当达到此阈值后，打开断路器，默认50%
    }
    )
    public String helloService(){
        return restTemplate.getForObject("http://alibaba-provider/echo/ribbonHystrix",String.class);
    }

    public String ribbonFallBack(){
        return "error";
    }
}
