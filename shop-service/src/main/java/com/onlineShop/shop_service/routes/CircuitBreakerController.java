package com.onlineShop.shop_service.routes;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {
    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
    @GetMapping("/sample-api")
    @CircuitBreaker(name = "default",fallbackMethod = "fail")
    public String sampleApi(){
        logger.info("sample call");
        ResponseEntity<String> forEntity=new RestTemplate().getForEntity("http://localhost:8080/api/v1/users/register",String.class);
        return forEntity.getBody();
    }
    public  String fail(Exception ex){
        return "fail get response from: http://localhost:8080/api/v1/users/register";
    }
}
