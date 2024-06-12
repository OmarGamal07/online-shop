package com.onlineShop.shop_service.routes;


import com.onlineShop.shop_service.config.JwtContext;
import com.onlineShop.shop_service.entities.Cart;
import com.onlineShop.shop_service.entities.CartItem;
import com.onlineShop.shop_service.entities.CartItemRequest;
import com.onlineShop.shop_service.services.CartService;
import com.onlineShop.shop_service.services.WalletClient;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    private  WalletClient walletClient;


    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    private void getToken(@NonNull HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("token not vaild");
        }

        final String jwt = authHeader.substring(7);
        JwtContext.setJwtToken(jwt);
    }

    @Transactional
    @PostMapping("/{userId}")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Integer userId, @RequestBody CartItemRequest cartItemRequest, @NonNull HttpServletRequest request) {
//        todo:check if product exist not add it again
        try {
            getToken(request);
            // Check if user exists
            if (!walletClient.getUserById(userId)) {
                return ResponseEntity.badRequest().body(null);
            }
            Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
            if(!userIdFromToken.equals(userId)){
                throw new RuntimeException("Cant get item to diffrent user");
            }
            Cart updatedCart = cartService.addItemToCart(userId, cartItemRequest.getProductId(), cartItemRequest.getQuantity());

            return ResponseEntity.ok(updatedCart);
        } finally {
            JwtContext.clear();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getItemsByUserId(@PathVariable Integer userId,@NonNull HttpServletRequest request ) {
       try {
           getToken(request);
           // Check if user exists
           if (!walletClient.getUserById(userId)) {
               return ResponseEntity.badRequest().body(null);
           }
           Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
           if(!userIdFromToken.equals(userId)){
               throw new RuntimeException("Cant get item to diffrent user");
           }

           List<CartItem> items = cartService.getItemsByUserId(userId);
           return ResponseEntity.ok(items);
       }finally {
           JwtContext.clear();
       }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Cart> deleteAllItemsInCart(@PathVariable Integer userId,@NonNull HttpServletRequest request) {
        try {
            getToken(request);
            // Check if user exists
            if (!walletClient.getUserById(userId)) {
                return ResponseEntity.badRequest().body(null);
            }
            Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
            if(!userIdFromToken.equals(userId)){
                throw new RuntimeException("Cant get item to diffrent user");
            }

            Cart updatedCart = cartService.deleteAllItemsInCart(userId);
            return ResponseEntity.ok(updatedCart);
        }finally {
            JwtContext.clear();
        }

    }

    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<Cart> deleteItemFromCart(@PathVariable Integer userId, @PathVariable Integer itemId, @NonNull HttpServletRequest request) {
        try {
            getToken(request);
            // Check if user exists
            if (!walletClient.getUserById(userId)) {
                return ResponseEntity.badRequest().body(null);
            }
            Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
            if (!userIdFromToken.equals(userId)) {
                throw new RuntimeException("Cannot delete item for a different user");
            }
            Cart updatedCart = cartService.deleteItemFromCart(userId, itemId);
            return ResponseEntity.ok(updatedCart);
        } finally {
            JwtContext.clear();
        }
    }

    @PutMapping("/{userId}/item/{itemId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Integer userId, @PathVariable Integer itemId, @RequestBody CartItemRequest cartItem,@NonNull HttpServletRequest request) {
        try {
            getToken(request);
            // Check if user exists
            if (!walletClient.getUserById(userId)) {
                return ResponseEntity.badRequest().body(null);
            }
            Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
            if(!userIdFromToken.equals(userId)){
                throw new RuntimeException("Cant get item to diffrent user");
            }

            Cart updatedCart = cartService.updateCartItem(userId, itemId, cartItem.getProductId(),cartItem.getQuantity());
            return ResponseEntity.ok(updatedCart);
        }finally {
            JwtContext.clear();
        }

    }

//    @PostMapping("/{userId}/item")
//    public ResponseEntity<Cart> addOrUpdateItemInCart(@PathVariable Integer userId, @RequestBody CartItemRequest cartItemRequest, @NonNull HttpServletRequest request) {
//        try {
//            getToken(request);
//
//            // Check if user exists
//            if (!walletClient.getUserById(userId)) {
//                return ResponseEntity.badRequest().body(null);
//            }
//
//            Integer userIdFromToken = walletClient.getUserIdByToken(JwtContext.getJwtToken());
//            if (!userIdFromToken.equals(userId)) {
//                throw new RuntimeException("Cannot add or update item for a different user");
//            }
//
//            // Check if the product already exists in the cart
//            List<CartItem> items = cartService.getItemsByUserId(userId);
//            boolean itemExists = false;
//            for (CartItem item : items) {
//                if (item.getProduct().getId().equals(cartItemRequest.getProductId())) {
//                    itemExists = true;
//                    break;
//                }
//            }
//
//            Cart updatedCart;
//            if (itemExists) {
//                updatedCart = cartService.updateCartItem(userId, cartItemRequest.getProductId(), cartItemRequest.getQuantity());
//            } else {
//                updatedCart = cartService.addItemToCart(userId, cartItemRequest.getProductId(), cartItemRequest.getQuantity());
//            }
//
//            return ResponseEntity.ok(updatedCart);
//        } finally {
//            JwtContext.clear();
//        }
//    }
}
