package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh tại đây
}