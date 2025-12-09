package com.benchmark.jersey.service;

import com.benchmark.jersey.dto.PageResponse;
import com.benchmark.jersey.entity.Item;
import com.benchmark.jersey.repository.ItemRepository;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Item operations
 */
public class ItemService {
    
    @Inject
    private ItemRepository itemRepository;
    
    /**
     * Find all items with pagination
     */
    public PageResponse<Item> findAll(int page, int size) {
        List<Item> items = itemRepository.findAll(page, size);
        long totalElements = itemRepository.count();
        return new PageResponse<>(items, page, size, totalElements);
    }
    
    /**
     * Find items by category ID with pagination
     */
    public PageResponse<Item> findByCategoryId(Long categoryId, int page, int size) {
        List<Item> items = itemRepository.findByCategoryId(categoryId, page, size);
        long totalElements = itemRepository.countByCategoryId(categoryId);
        return new PageResponse<>(items, page, size, totalElements);
    }
    
    /**
     * Find item by ID
     */
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
    
    /**
     * Create or update item
     */
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    
    /**
     * Delete item
     */
    public void delete(Long id) {
        itemRepository.delete(id);
    }
    
    /**
     * Check if item exists
     */
    public boolean exists(Long id) {
        return itemRepository.existsById(id);
    }
}
