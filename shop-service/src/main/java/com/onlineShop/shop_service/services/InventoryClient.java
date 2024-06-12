package com.onlineShop.shop_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productCode}/availability")
    boolean isProductAvailable(@PathVariable Integer productCode, @RequestParam int quantity);

    @PostMapping("/api/v1/inventory/{productCode}/reserve")
    void reserveProduct(@PathVariable Integer productCode, @RequestParam int quantity);

    @PutMapping("/api/v1/inventory/{productCode}/update")
    void updateStock(@PathVariable Integer productCode, @RequestParam int quantity);

    @PostMapping("/api/v1/inventory/{productCode}/return")
    void handleReturn(@PathVariable Integer productCode, @RequestParam int quantity);
    @PostMapping("/api/v1/inventory")
    void createInventory(@RequestParam("productCode") Integer productCode, @RequestParam("quantity") int quantity, @RequestParam("reorderThreshold") int reorderThreshold);

    @DeleteMapping("/api/v1/inventory/{productCode}")
    void deleteInventory(@PathVariable("productCode") Integer productCode);
    @GetMapping("/api/v1/inventory/{productCode}/quantity")
    int getProductQuantity(@PathVariable("productCode") Integer productCode);
    @PutMapping("/api/v1/inventory/{productCode}/quantity")
    void updateProductQuantity(@PathVariable Integer productCode, @RequestParam int quantity);
}
