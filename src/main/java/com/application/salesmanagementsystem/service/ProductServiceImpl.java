package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.ImageRepository;
import com.application.salesmanagementsystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
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

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findAllByKeyword(keyword, pageable);
    }
    @Override
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryCategoryName(category, pageable);
    }
    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }
    @Override
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }
    @Override
    public List<Product> getTop10NewestProducts() {
        return productRepository.findTop10ByOrderByCreateDateDesc();
    }
    @Override
    public List<Integer> findImagesByProductID(Integer id) {
        return imageRepository.findAllByProductId(id);
    }
    @Override
    public int generateNewProductId() {
        Product product = productRepository.findTopByOrderByProductIDDesc();
        return product.getProductID() + 1;
    }

    @Override
    public Integer totalProducts() {
        return productRepository.getTotalQuantity();
    }

    @Override
    public List<Object[]> getBestSellingProduct(int topN) {
        return productRepository.getTopSellingProducts(topN);
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }
    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

}