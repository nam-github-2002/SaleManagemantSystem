package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Product;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE " +
            "CAST(p.productID AS string) = :keyword OR " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.supplier.supplierName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findAllByKeyword(String keyword, Pageable pageable);

    Page<Product> findAll(@Nullable Pageable pageable);
    List<Product> findTop10ByOrderByCreateDateDesc();
    Product findTopByOrderByProductIDDesc();
    Page<Product> findByCategoryCategoryName(String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> findByProductName(@Param("query") String query);


}