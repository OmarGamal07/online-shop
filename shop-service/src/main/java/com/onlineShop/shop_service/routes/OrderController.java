package com.onlineShop.shop_service.routes;


import com.onlineShop.shop_service.config.JwtContext;
import com.onlineShop.shop_service.entities.Order;
import com.onlineShop.shop_service.services.OrderService;
import com.onlineShop.shop_service.services.WalletClient;
import io.micrometer.common.lang.NonNull;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final WalletClient walletClient;

    @Autowired
    public OrderController(OrderService orderService, WalletClient walletClient) {
        this.orderService = orderService;
        this.walletClient = walletClient;
    }
    @Transactional
    @PostMapping("/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Integer userId, HttpServletRequest request) {
        // Validate token and user
        if (!validateUserToken(userId, request)) {
            return ResponseEntity.status(403).body(null);
        }

        Order order = orderService.createOrder(userId,request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId, HttpServletRequest request) {
        // Validate token and user
        if (!validateUserToken(userId, request)) {
            return ResponseEntity.status(403).body(null);
        }

        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer userId, @PathVariable Integer orderId, HttpServletRequest request) {
        // Validate token and user
        if (!validateUserToken(userId, request)) {
            return ResponseEntity.status(403).body(null);
        }

        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    private boolean validateUserToken(Integer userId, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("token not vaild");
        }
        final String token = authHeader.substring(7);
        JwtContext.setJwtToken(token);
        Integer tokenUserId = walletClient.getUserIdByToken(token);
        return tokenUserId != null && tokenUserId.equals(userId);
    }


}
