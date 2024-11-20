package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByCustomerId(String customerId);
    void deleteByCustomerId(String customerId);
}
