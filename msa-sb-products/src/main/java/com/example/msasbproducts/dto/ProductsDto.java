package com.example.msasbproducts.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ProductsDto {
    private String pdtName;
    private Integer pdtPrice;

    @Builder
    public ProductsDto(String pdtName, Integer pdtPrice) {
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
    }
}


