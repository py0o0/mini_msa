package com.example.msasbproducts.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CartDto {
    private int cartId;
    private String email;
    private int pdtId;
    private String pdtName;
    private int quantity;
    private int price;

    @Builder
    public CartDto(int cartId, String email, int pdtId, int quantity, String pdtName, int price) {
        this.cartId = cartId;
        this.email = email;
        this.pdtId = pdtId;
        this.quantity = quantity;
        this.pdtName = pdtName;
        this.price = price;
    }
}
