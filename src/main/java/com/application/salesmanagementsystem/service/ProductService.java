package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByCategory(String category, Pageable pageable);
    Page<Product> searchProducts(String keyword, Pageable pageable);
    Optional<Product> getProductById(Integer id);
    Optional<Product> findById(int id);
    List<Product> getTop10NewestProducts();
    List<Integer> findImagesByProductID(Integer id);
    int generateNewProductId();
    void saveProduct(Product product);
    void deleteProduct(Integer id);
}
