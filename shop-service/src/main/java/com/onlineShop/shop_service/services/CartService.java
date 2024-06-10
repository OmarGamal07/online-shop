package com.onlineShop.shop_service.services;

import com.onlineShop.shop_service.entities.Cart;
import com.onlineShop.shop_service.entities.CartItem;
import com.onlineShop.shop_service.entities.Product;
import com.onlineShop.shop_service.repositories.CartItemRepository;
import com.onlineShop.shop_service.repositories.CartRepository;
import com.onlineShop.shop_service.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }
    public Cart createCart(Integer userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }
    public Optional<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId);
    }
    public Cart addItemToCart(Integer userId, Integer productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException(STR."Product not found with id: \{productId}");
        }
        CartItem cartItem = CartItem.builder()
                .product(product.get())
                .quantity(quantity)
                .build();
        Cart cart = getCartByUserId(userId).orElseGet(() -> createCart(userId));
        cart.getItems().add(cartItem);
        cartItemRepository.save(cartItem);
        return cartRepository.save(cart);
    }

    public List<CartItem> getItemsById(Integer cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException(STR."Cart not found with id: \{cartId}"))
                .getItems();
    }
    public List<CartItem> getItemsByUserId(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(STR."Cart not found for user id: \{userId}"))
                .getItems();
    }
    public Cart deleteAllItemsInCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(STR."Cart not found for user id: \{userId}"));
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    public Cart deleteItemFromCart(Integer userId, Integer cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(STR."Cart not found for user id: \{userId}"));
        List<CartItem> items = cart.getItems();
        boolean isItemRemoved = items.removeIf(item -> item.getId().equals(cartItemId));
        if (!isItemRemoved) {
            throw new RuntimeException(STR."Item not found in the cart with id: \{cartItemId}");
        }

        cartRepository.save(cart); // Save the cart first to update the relationship
        cartItemRepository.deleteById(cartItemId); // Then delete the cart item

        return cart;
    }

    public Cart updateCartItem(Integer userId, Integer cartItemId, Integer productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(STR."Cart not found for user id: \{userId}"));
        List<CartItem> items = cart.getItems();

        Optional<CartItem> optionalCartItem = items.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
//            cartItem.setProduct(updatedCartItem.getProduct());
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        } else {
            throw new RuntimeException(STR."Item not found in the cart with id: \{cartItemId}");
        }

        return cartRepository.save(cart);
    }

}
