package com.benchmark.jersey.service;

import com.benchmark.jersey.dto.PageResponse;
import com.benchmark.jersey.entity.Category;
import com.benchmark.jersey.repository.CategoryRepository;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Category operations
 */
public class CategoryService {
    
    @Inject
    private CategoryRepository categoryRepository;
    
    /**
     * Find all categories with pagination
     */
    public PageResponse<Category> findAll(int page, int size) {
        List<Category> categories = categoryRepository.findAll(page, size);
        long totalElements = categoryRepository.count();
        return new PageResponse<>(categories, page, size, totalElements);
    }
    
    /**
     * Find category by ID
     */
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Create or update category
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    /**
     * Delete category
     */
    public void delete(Long id) {
        categoryRepository.delete(id);
    }
    
    /**
     * Check if category exists
     */
    public boolean exists(Long id) {
        return categoryRepository.existsById(id);
    }
}
