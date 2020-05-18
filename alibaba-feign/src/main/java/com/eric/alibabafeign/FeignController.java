package com.eric.alibabafeign;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    @Autowired
    private RemoteClient remoteClient;

    @Autowired
    private SentinelService sentinelService;

    @GetMapping("/feign")
    @SentinelResource("feign")
    public String feign() {
        return remoteClient.helloNacos();
    }

    @GetMapping("/sentinel")
    public String sentinel(String id){
        return sentinelService.sentinel(id);
    }

    @GetMapping("/hello")
    public String hello(String id){
        return sentinelService.hello(id);
    }

    @GetMapping("/param")
    public String param(String id){
        return sentinelService.param(id);
    }
}
