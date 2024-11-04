package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Lấy tất cả sản phẩm
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Lấy sản phẩm theo ID
    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    // Lưu sản phẩm mới hoặc cập nhật sản phẩm hiện có
    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // Xóa sản phẩm theo ID
    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByProductNameContaining(keyword, pageable);
    }

    @Override
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    public int generateNewProductId() {
        Product product = productRepository.findTopByOrderByProductIDDesc();
        return product.getProductID() + 1;
    }

    @Override
    public List<Image> getProductImages(int id) {
        return imageRepository.findByProduct_ProductID(id);
    }
}