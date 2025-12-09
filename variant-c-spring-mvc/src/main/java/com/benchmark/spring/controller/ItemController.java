package com.benchmark.spring.controller;

import com.benchmark.spring.entity.Category;
import com.benchmark.spring.entity.Item;
import com.benchmark.spring.service.CategoryService;
import com.benchmark.spring.service.ItemService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

/**
 * REST Controller for Item endpoints
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    
    private final ItemService itemService;
    private final CategoryService categoryService;
    
    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }
    
    /**
     * GET /items?page=X&size=Y&categoryId=Z
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long categoryId) {
        
        if (page < 0 || size <= 0 || size > 1000) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items;
        
        if (categoryId != null) {
            if (!categoryService.existsById(categoryId)) {
                return ResponseEntity.notFound().build();
            }
            items = itemService.findByCategoryId(categoryId, pageable);
        } else {
            items = itemService.findAll(pageable);
        }
        
        return ResponseEntity.ok(items);
    }
    
    /**
     * GET /items/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getById(@PathVariable Long id) {
        Item item = itemService.findById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }
    
    /**
     * POST /items
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ItemRequest request) {
        Category category = categoryService.findById(request.getCategoryId());
        if (category == null) {
            return ResponseEntity.badRequest().body("Category not found");
        }
        
        Item item = new Item();
        item.setSku(request.getSku());
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock());
        item.setCategory(category);
        
        Item created = itemService.save(item);
        return ResponseEntity
                .created(URI.create("/items/" + created.getId()))
                .body(created);
    }
    
    /**
     * PUT /items/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request) {
        
        Item existingItem = itemService.findById(id);
        if (existingItem == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Update category if changed
        if (request.getCategoryId() != null && 
            !request.getCategoryId().equals(existingItem.getCategory().getId())) {
            
            Category category = categoryService.findById(request.getCategoryId());
            if (category == null) {
                return ResponseEntity.badRequest().body("Category not found");
            }
            existingItem.setCategory(category);
        }
        
        // Update fields
        if (request.getSku() != null) existingItem.setSku(request.getSku());
        if (request.getName() != null) existingItem.setName(request.getName());
        if (request.getPrice() != null) existingItem.setPrice(request.getPrice());
        if (request.getStock() != null) existingItem.setStock(request.getStock());
        
        Item updated = itemService.save(existingItem);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * DELETE /items/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!itemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * DTO for Item creation/update requests
     */
    public static class ItemRequest {
        @NotBlank
        private String sku;
        
        @NotBlank
        private String name;
        
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal price;
        
        @NotNull
        @Min(0)
        private Integer stock;
        
        @NotNull
        @JsonProperty("categoryId")
        private Long categoryId;
        
        // Getters and Setters
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }
}
