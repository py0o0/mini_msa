package com.example.msasbproducts.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;
    private String email;
    private int pdtId;
    private String pdtName;
    private int quantity;
    private int price;
}
