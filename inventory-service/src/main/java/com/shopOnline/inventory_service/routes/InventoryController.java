package com.shopOnline.inventory_service.routes;


import com.shopOnline.inventory_service.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @PostMapping
    public void createInventory(@RequestParam Integer productCode, @RequestParam int quantity, @RequestParam int reorderThreshold) {
        inventoryService.createInventory(productCode, quantity, reorderThreshold);
    }

    @DeleteMapping("/{productCode}")
    public void deleteInventory(@PathVariable Integer productCode) {
        inventoryService.deleteInventory(productCode);
    }
    @GetMapping("/{productCode}/availability")
    public boolean isProductAvailable(@PathVariable Integer productCode, @RequestParam int quantity) {
        return inventoryService.isProductAvailable(productCode, quantity);
    }

    @PostMapping("/{productCode}/reserve")
    public void reserveProduct(@PathVariable Integer productCode, @RequestParam int quantity) {
        inventoryService.reserveProduct(productCode, quantity);
    }

    @PutMapping("/{productCode}/update")
    public void updateStock(@PathVariable Integer productCode, @RequestParam int quantity) {
        inventoryService.updateStock(productCode, quantity);
    }

    @PostMapping("/{productCode}/return")
    public void handleReturn(@PathVariable Integer productCode, @RequestParam int quantity) {
        inventoryService.handleReturn(productCode, quantity);
    }
    @GetMapping("/{productCode}/quantity")
    public int getProductQuantity(@PathVariable Integer productCode) {
        return inventoryService.getProductQuantity(productCode);
    }
    @PutMapping("/{productCode}/quantity")
    public void updateProductQuantity(@PathVariable Integer productCode, @RequestParam int quantity) {
        inventoryService.updateProductQuantity(productCode, quantity);
    }
}
