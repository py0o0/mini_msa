package com.example.msasbproducts.controller;

import com.example.msasbproducts.dto.PaymentDto;
import com.example.msasbproducts.dto.ProductsDto;
import com.example.msasbproducts.kafka.KafkaProducer;
import com.example.msasbproducts.service.ProductsService;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pdts")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final KafkaProducer kafkaProducer;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {

        return ResponseEntity.ok(productsService.allProducts());
    }

    @GetMapping("/detail/{pdtId}")
    public ResponseEntity<?> productDetail(@PathVariable int pdtId) {
        System.out.println("내가 왔따");
        return productsService.productsDetail(pdtId);
    }

    @PostMapping("/cart")
    public ResponseEntity<?> productCart(@RequestHeader("X-Auth-User")String email, int pdtId, int quantity){
        return productsService.productCart(email, pdtId, quantity);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> productCart(@RequestHeader("X-Auth-User")String email){
        return productsService.getCart(email);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertProduct(String pdtName, int price, int quantity) {
        return productsService.insertProduct(pdtName,price,quantity);
    }

    // 결제 -> 결제 메세지 -> 이벤트 발송 -> 유저한테 구독
    @PostMapping("/payment")
    public ResponseEntity<?> payment(
            PaymentDto paymentDto){
        // DB 처리 생략
        // Kafka로 발송 처리만
        try {
            kafkaProducer.sengMsg("msa-sb-products-payment", paymentDto);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("성공");
    }


}
