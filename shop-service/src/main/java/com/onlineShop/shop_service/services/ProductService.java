package com.onlineShop.shop_service.services;

import com.onlineShop.shop_service.entities.Product;
import com.onlineShop.shop_service.entities.ProductRequest;
import com.onlineShop.shop_service.entities.ProductResponse;
import com.onlineShop.shop_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    @Autowired
    public ProductService(ProductRepository productRepository, InventoryClient inventoryClient) {
        this.productRepository = productRepository;
        this.inventoryClient = inventoryClient;
    }

    public Product createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        Product savedProduct = productRepository.save(product);

        try {
            inventoryClient.createInventory(savedProduct.getId(), productRequest.getQuantity(), 10); // Reorder threshold: 10
        } catch (Exception e) {
            // Rollback product creation if inventory creation fails
            productRepository.delete(savedProduct);
            throw new RuntimeException("Failed to create inventory for the product", e);
        }

        return savedProduct;
    }
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> {
                    int quantity = inventoryClient.getProductQuantity(product.getId());
                    return new ProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            quantity
                    );
                })
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int quantity = inventoryClient.getProductQuantity(id);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }

    public Product updateProduct(Integer id, ProductRequest updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update the product details
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());


        // Update the product in the repository
        product = productRepository.save(product);

        // Update the quantity in the inventory service
        inventoryClient.updateProductQuantity(product.getId(),updatedProduct.getQuantity());

        return product;
    }

    public void deleteProduct(Integer id) {
        Product product =  productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        Integer productCode = product.getId(); // Assuming product name is used as product code

        productRepository.deleteById(id);

        try {
            inventoryClient.deleteInventory(productCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete inventory for the product", e);
        }
    }
    public void returnProduct(Integer productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        inventoryClient.handleReturn(product.getId(), quantity);
    }
}

