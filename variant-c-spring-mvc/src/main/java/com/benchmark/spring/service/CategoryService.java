package com.benchmark.spring.service;

import com.benchmark.spring.entity.Category;
import com.benchmark.spring.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Category operations
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}
