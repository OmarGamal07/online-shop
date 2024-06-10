package com.onlineShop.shop_service.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String token = JwtContext.getJwtToken();
                if (token != null) {
                    template.header("Authorization", STR."Bearer \{token}");
                }
            }
        };
    }
}
