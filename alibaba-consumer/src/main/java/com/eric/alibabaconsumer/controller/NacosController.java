package com.eric.alibabaconsumer.controller;

import com.eric.alibabaconsumer.util.ApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class NacosController {

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    @Value("${user.age:false}")
    private String useLocalCache;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/echo/app-name")
    public String echoAppName(){
        //通过LoadBalanceClient和RestTemplate
//        ServiceInstance serviceInstance = loadBalancerClient.choose("alibaba-provider");
//        String path = String.format("http://%s:%s/echo/%s",serviceInstance.getHost(),serviceInstance.getPort(),appName);
        String path = "http://alibaba-provider/echo/" + appName;
        System.out.println("请求路径: "+ path);

        String userName = ApplicationContextUtils.getContext().getEnvironment().getProperty("user.name");
        String userAge = ApplicationContextUtils.getContext().getEnvironment().getProperty("my.age");
        System.err.println("user name :" +userName+"; age: "+userAge);
        return restTemplate.getForObject(path,String.class);
    }

    @GetMapping("/echo/{path}")
    public String echoAppNameByRest(@PathVariable String path){
        //通过RestTemplate
        String url = "http://"+path+"/echo/"+appName;
        System.out.println("请求路径: "+ url);
        return restTemplate.getForObject(url,String.class);
    }

    public void setUseLocalCache(String useLocalCache) {
        this.useLocalCache = useLocalCache;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public String get() {
        return useLocalCache;
    }
}
