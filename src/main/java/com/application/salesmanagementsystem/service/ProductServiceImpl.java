package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả sản phẩm
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy sản phẩm theo ID
    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    // Lưu sản phẩm mới hoặc cập nhật sản phẩm hiện có
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Xóa sản phẩm theo ID
    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}