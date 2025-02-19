package com.example.msasbproducts.repository;

import com.example.msasbproducts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByEmail(String email);

    Optional<Cart> findByEmailAndPdtId(String email, int pdtId);
}
