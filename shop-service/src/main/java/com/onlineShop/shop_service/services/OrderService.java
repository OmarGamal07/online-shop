package com.onlineShop.shop_service.services;

import com.onlineShop.shop_service.config.JwtContext;
import com.onlineShop.shop_service.entities.CartItem;
import com.onlineShop.shop_service.entities.Order;
import com.onlineShop.shop_service.entities.OrderItem;
import com.onlineShop.shop_service.entities.Product;
import com.onlineShop.shop_service.repositories.OrderRepository;
import com.onlineShop.shop_service.repositories.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final WalletClient walletClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, CartService cartService, WalletClient walletClient) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.walletClient = walletClient;
    }

    public Order createOrder(Integer userId, HttpServletRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Get the cart items for the user
        List<CartItem> cartItems = cartService.getItemsByUserId(userId);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

            // Create order items
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .build();
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(price);
        }

        // Check if the user has enough balance in the wallet
        BigDecimal walletBalance = getWalletBalance(userId, request);
        if (walletBalance.compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient balance in the wallet.");
        }

        // Deduct the total amount from the wallet
        withdrawFromWallet(userId, totalAmount, request);

        // Create and save the order
        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        // Clear the cart after order creation
        cartService.deleteAllItemsInCart(userId);

        return order;
    }

    private BigDecimal getWalletBalance(Integer userId, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        ResponseEntity<BigDecimal> response = walletClient.getWalletBalance(userId);
        return response.getBody();
    }

    private void withdrawFromWallet(Integer userId, BigDecimal amount, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        try {
            walletClient.withdraw(userId, amount);
        } catch (Exception e) {
            throw new RuntimeException("Error withdrawing from wallet: " + e.getMessage());
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            JwtContext.setJwtToken(token);
            return token;
        } else {
            throw new RuntimeException("Invalid or missing Authorization header");
        }

    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }
}
