package com.onlineShop.shop_service.config;


public class JwtContext {
    private static final ThreadLocal<String> jwtTokenHolder = new ThreadLocal<>();

    public static void setJwtToken(String token) {
        jwtTokenHolder.set(token);
    }

    public static String getJwtToken() {
        return jwtTokenHolder.get();
    }

    public static void clear() {
        jwtTokenHolder.remove();
    }
}
