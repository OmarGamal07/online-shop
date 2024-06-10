package com.onlineShop.shop_service.repositories;

import com.onlineShop.shop_service.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
