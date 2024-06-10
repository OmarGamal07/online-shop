package com.onlineShop.shop_service.services;

import com.onlineShop.shop_service.config.FeignClientConfig;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "wallet-service" ,configuration = FeignClientConfig.class)
public interface WalletClient {
    @GetMapping("/api/v1/auth/getUserIdByToken/{token}")
    Integer getUserIdByToken(@PathVariable("token") String token);
    @GetMapping("/api/v1/users/{userId}")
    Boolean getUserById(@PathVariable Integer userId);
    @PostMapping("/api/v1/withdraw/{userId}/{amount}")
    ResponseEntity<Map<String, Object>> withdraw(@PathVariable("userId") Integer userId,
                                                 @PathVariable("amount") BigDecimal amount);

    @GetMapping("/api/v1/wallet/{userId}/balance")
    ResponseEntity<BigDecimal> getWalletBalance(@PathVariable("userId") Integer userId);

}

