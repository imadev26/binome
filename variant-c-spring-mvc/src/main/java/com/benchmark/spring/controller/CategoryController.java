package com.benchmark.spring.controller;

import com.benchmark.spring.entity.Category;
import com.benchmark.spring.service.CategoryService;
import com.benchmark.spring.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST Controller for Category endpoints
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final ItemService itemService;
    
    public CategoryController(CategoryService categoryService, ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }
    
    /**
     * GET /categories?page=X&size=Y
     */
    @GetMapping
    public ResponseEntity<Page<Category>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        if (page < 0 || size <= 0 || size > 1000) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * GET /categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }
    
    /**
     * POST /categories
     */
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) {
        category.setId(null); // Ensure new entity
        Category created = categoryService.save(category);
        return ResponseEntity
                .created(URI.create("/categories/" + created.getId()))
                .body(created);
    }
    
    /**
     * PUT /categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        category.setId(id);
        Category updated = categoryService.save(category);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * DELETE /categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /categories/{id}/items?page=X&size=Y
     * Relational endpoint
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<?> getItems(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        if (!categoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        if (page < 0 || size <= 0 || size > 1000) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(itemService.findByCategoryId(id, pageable));
    }
}
