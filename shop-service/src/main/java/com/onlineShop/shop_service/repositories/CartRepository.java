package com.onlineShop.shop_service.repositories;

import com.onlineShop.shop_service.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserId(Integer userId);
}
