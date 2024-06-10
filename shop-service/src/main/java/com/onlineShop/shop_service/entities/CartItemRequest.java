package com.onlineShop.shop_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequest {
    private Integer productId;
    private Integer quantity;
}
