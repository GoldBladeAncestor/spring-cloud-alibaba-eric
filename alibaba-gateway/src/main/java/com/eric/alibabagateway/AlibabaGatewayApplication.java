package com.eric.alibabagateway;

import com.eric.alibabagateway.filter.GatewayRateLimitFilterByIp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlibabaGatewayApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置gateway filter
     * @param builder
     * @return
     */
//    @Bean
//    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/test")
//                        .filters(f -> f.filter(new CustomGatewayFilter()))
//                        .uri("http://localhost:8080/echo/sss")
//                        .order(0)
//                        .id("custom_filter")
//                )
//                .build();
//    }

    /**
     * 通过流式API配置路由规则，当访问/test/rateLimit时，自动转发到http://localhost:8000/hello/rateLimit
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/test/rateLimit")
                        .filters(f -> f.filter(new GatewayRateLimitFilterByIp(5,1, Duration.ofSeconds(1))))
                        .uri("http://localhost:8084/hello")
                        .id("Unable to find GatewayFilterFactory with name RequestRateLimiter")
                ).build();
    }


}
