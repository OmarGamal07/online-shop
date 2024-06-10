package com.onlineShop.shop_service.repositories;

import com.onlineShop.shop_service.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
