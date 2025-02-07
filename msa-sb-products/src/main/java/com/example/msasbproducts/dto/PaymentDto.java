package com.example.msasbproducts.dto;

import lombok.Data;

@Data
public class PaymentDto {
    String email;
    int pdtId;
    int amount;
}
