package com.shopOnline.inventory_service.services;


import com.shopOnline.inventory_service.entities.ProductInventory;
import com.shopOnline.inventory_service.repositories.ProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {
    private final ProductInventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(ProductInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void createInventory(Integer productCode, int quantity, int reorderThreshold) {
        if (inventoryRepository.findByProductCode(productCode).isPresent()) {
            throw new RuntimeException("Inventory record already exists for this product");
        }

        ProductInventory productInventory = ProductInventory.builder()
                .productCode(productCode)
                .quantity(quantity)
                .reorderThreshold(reorderThreshold)
                .reservedQuantity(0)
                .build();
        inventoryRepository.save(productInventory);
    }

    public void deleteInventory(Integer productCode) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for product: " + productCode));
        inventoryRepository.delete(productInventory);
    }
    public boolean isProductAvailable(Integer productCode, int quantity) {
        Optional<ProductInventory> productInventory = inventoryRepository.findByProductCode(productCode);
        return productInventory.map(inventory -> inventory.getQuantity() - inventory.getReservedQuantity() >= quantity).orElse(false);
    }

    public void reserveProduct(Integer productCode, int quantity) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productInventory.getQuantity() - productInventory.getReservedQuantity() >= quantity) {
            productInventory.setReservedQuantity(productInventory.getReservedQuantity() + quantity);
            inventoryRepository.save(productInventory);
        } else {
            throw new RuntimeException("Insufficient stock to reserve");
        }
    }

    public void updateStock(Integer productCode, int quantity) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productInventory.setQuantity(productInventory.getQuantity() - quantity);
        productInventory.setReservedQuantity(productInventory.getReservedQuantity() - quantity);
        inventoryRepository.save(productInventory);

        if (productInventory.getQuantity() < productInventory.getReorderThreshold()) {
            // Trigger reorder logic
            System.out.println("Reorder needed for product: " + productCode);
        }
    }

    public void handleReturn(Integer productCode, int quantity) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productInventory.setQuantity(productInventory.getQuantity() + quantity);
        int reservedQuantity = productInventory.getReservedQuantity() - quantity;
        if (reservedQuantity < 0) {
            throw new RuntimeException("Insufficient stock to reserve");
        }
        productInventory.setReservedQuantity(reservedQuantity);
        inventoryRepository.save(productInventory);
    }
    public int getProductQuantity(Integer productCode) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productInventory.getQuantity();
    }

    public void updateProductQuantity(Integer productCode, int quantity) {
        ProductInventory productInventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productInventory.setQuantity( quantity);

        inventoryRepository.save(productInventory);
    }
}

