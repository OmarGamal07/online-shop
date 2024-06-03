package com.onlineShop.wallet.exceptions;

import java.util.List;

public class CustomErrorResponse {
    private String message;
    private List<String> details;

    public CustomErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
