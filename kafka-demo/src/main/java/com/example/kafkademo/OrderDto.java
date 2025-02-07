package com.example.kafkademo;

import lombok.Data;

@Data
public class OrderDto {
    private String orderId;
    private String message;
}
