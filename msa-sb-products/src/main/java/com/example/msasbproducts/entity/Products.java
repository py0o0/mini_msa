package com.example.msasbproducts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="products")
@NoArgsConstructor
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // Json 응답 처리시 해당 필드 누락
    private Integer pdtId;
    private String pdtName;
    private Integer pdtPrice;
    private Integer pdtQuantity;

    @Builder
    public Products(int pdtId, String pdtName, Integer pdtPrice, Integer pdtQuantity) {
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
        this.pdtQuantity = pdtQuantity;
    }

}
