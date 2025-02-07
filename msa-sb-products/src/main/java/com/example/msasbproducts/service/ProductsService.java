package com.example.msasbproducts.service;

import com.example.msasbproducts.dto.CartDto;
import com.example.msasbproducts.dto.ProductsDto;
import com.example.msasbproducts.entity.Cart;
import com.example.msasbproducts.entity.Products;
import com.example.msasbproducts.repository.CartRepository;
import com.example.msasbproducts.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final CartRepository cartRepository;

    public List<ProductsDto> allProducts() {
        List<Products> pdts = productsRepository.findAll();

        return pdts.stream()
                .map(p -> ProductsDto.builder()
                        .pdtName(p.getPdtName())
                        .pdtPrice(p.getPdtPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> productsDetail(int pdtId) {
        Products products = productsRepository.findById(pdtId).orElse(null);

        if(products == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "pdtId", products.getPdtId(),
                "pdtName", products.getPdtName(),
                "pdtPrice", products.getPdtPrice(),
                "pdtQuantity", products.getPdtQuantity()
        ));

    }

    public ResponseEntity<?> insertProduct(String pdtName, int price, int quantity) {

        return ResponseEntity.ok("성공");
    }

    @Transactional
    public ResponseEntity<?> productCart(String email, int pdtId, int quantity) {
        Products product = productsRepository.findById(pdtId).orElse(null);

        System.out.println(product);
        if(product == null)
            return ResponseEntity.notFound().build();

        int total = product.getPdtQuantity();
        int price = product.getPdtPrice();
        if(total < quantity)
            return ResponseEntity.badRequest().body("주문할 수 있는 양 초과");

        product.setPdtQuantity(total - quantity);
        productsRepository.save(product);

        Optional<Cart> myCartPdtO = cartRepository.findByEmailAndPdtId(email, pdtId);
        Cart myCartpdt = new Cart();

        if(myCartPdtO.isPresent()) {
            myCartpdt = myCartPdtO.get();
            int preQuantity = myCartpdt.getQuantity();
            preQuantity += quantity;
            myCartpdt.setQuantity(preQuantity);

            int prePrice = price * preQuantity;
            myCartpdt.setPrice(prePrice);
        }
        else{
            myCartpdt.setPdtId(pdtId);
            myCartpdt.setQuantity(quantity);
            myCartpdt.setPrice(price * quantity);
            myCartpdt.setEmail(email);
            myCartpdt.setPdtName(product.getPdtName());
        }

        cartRepository.save(myCartpdt);

        return ResponseEntity.ok("성공");
    }

    public ResponseEntity<?> getCart(String email) {
        List<Cart> cartList = cartRepository.findByEmail(email);
        List<CartDto> cartDtoList = new ArrayList<>();
        for(Cart cart : cartList){
            CartDto cartDto = CartDto.builder()
                    .cartId(cart.getCartId())
                    .pdtName(cart.getPdtName())
                    .quantity(cart.getQuantity())
                    .price(cart.getPrice())
                    .email(cart.getEmail())
                    .pdtId(cart.getPdtId())
                    .build();
            cartDtoList.add(cartDto);
        }
        return ResponseEntity.ok(cartDtoList);

    }
}
