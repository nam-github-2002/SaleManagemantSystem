package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Product;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByProductNameContaining(String keyword, Pageable pageable);
    Page<Product> findAll(@Nullable Pageable pageable);
    List<Product> findTop10ByOrderByCreateDateDesc();
    Product findTopByOrderByProductIDDesc();

}