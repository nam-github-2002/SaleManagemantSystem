package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    // Lấy tất cả các danh mục
    List<Category> getAllCategories();

    // Lấy danh mục theo ID
    Optional<Category> getCategoryById(Integer id);

    // Lưu danh mục mới hoặc cập nhật danh mục hiện có
    Category saveCategory(Category category);

    // Xóa danh mục theo ID
    void deleteCategory(Integer id);
}
