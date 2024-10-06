package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Lấy tất cả sản phẩm
    List<Product> getAllProducts();

    // Lấy sản phẩm theo ID
    Optional<Product> getProductById(Integer id);

    // Lưu sản phẩm mới hoặc cập nhật sản phẩm hiện có
    Product saveProduct(Product product);

    // Xóa sản phẩm theo ID
    void deleteProduct(Integer id);
}
