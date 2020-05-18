package com.eric.alibabaribbon.controller;

import com.eric.alibabaribbon.service.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RibbonService ribbonService;

    @GetMapping(value = "/ribbon")
    public String ribbon(){
        String result = restTemplate.getForObject("http://alibaba-provider/helloNacos", String.class);
        return result;
    }

    @GetMapping(value = "hello")
    public String helloConsumer(){
        return ribbonService.helloService();
    }
}
