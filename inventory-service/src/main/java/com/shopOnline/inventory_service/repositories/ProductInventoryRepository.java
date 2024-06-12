package com.shopOnline.inventory_service.repositories;


import com.shopOnline.inventory_service.entities.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProductCode(Integer productCode);
}
