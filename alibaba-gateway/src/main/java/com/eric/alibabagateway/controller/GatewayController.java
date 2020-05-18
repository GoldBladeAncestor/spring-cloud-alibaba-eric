package com.eric.alibabagateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping(value = "/gateway")
    public String gateway(){
        return "gateway";
    }
}
