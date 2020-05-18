package com.eric.alibabaprovider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class EchoController {

    @GetMapping(value ="/echo/{string}")
    public String echo(@PathVariable String string){
        System.out.println("Hello Nacos Discovery echo " + string + new Date());
        return "Hello Nacos Discovery echo " + string;
    }

    @GetMapping(value ="/helloNacos")
    public String helloNacos() throws InterruptedException {
        System.out.println("Hello Nacos Discovery " + new Date());
        return "Hello Nacos Discovery";
    }
}
