package com.onlineShop.wallet.services;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
