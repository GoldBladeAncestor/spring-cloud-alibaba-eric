package com.eric.alibabafeign;

import org.springframework.stereotype.Component;

@Component
public class RemoteHystrix implements RemoteClient {
    @Override
    public String helloNacos() {
        return "熔断熔断";
    }
}
