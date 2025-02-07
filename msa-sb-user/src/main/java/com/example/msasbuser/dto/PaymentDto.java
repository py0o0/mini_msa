package com.example.msasbuser.dto;

import lombok.Data;

@Data
public class PaymentDto {
    String email;
    int pdtId;
    int amount;
}
