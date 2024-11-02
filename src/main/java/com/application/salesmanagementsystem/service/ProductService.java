package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Lấy tất cả sản phẩm
    Page<Product> getAllProducts(Pageable pageable);

    // Lấy sản phẩm theo ID
    Optional<Product> getProductById(Integer id);

    // Lưu sản phẩm mới hoặc cập nhật sản phẩm hiện có
    void saveProduct(Product product);

    // Xóa sản phẩm theo ID
    void deleteProduct(Integer id);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Optional<Product> findById(int id);

    int generateNewProductId();

    List<Image> getProductImages(int id);
}
