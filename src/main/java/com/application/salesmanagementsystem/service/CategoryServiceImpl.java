package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Category;
import com.application.salesmanagementsystem.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả các danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục theo ID
    @Override
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Lưu danh mục mới hoặc cập nhật danh mục hiện có
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Xóa danh mục theo ID
    @Override
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}