package com.benchmark.spring.service;

import com.benchmark.spring.entity.Item;
import com.benchmark.spring.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Item operations
 */
@Service
@Transactional(readOnly = true)
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final boolean useJoinFetch;
    
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        // Check environment variable for JOIN FETCH mode
        this.useJoinFetch = Boolean.parseBoolean(
            System.getenv().getOrDefault("USE_JOIN_FETCH", "true")
        );
    }
    
    public Page<Item> findAll(Pageable pageable) {
        if (useJoinFetch) {
            return itemRepository.findAllWithCategory(pageable);
        }
        return itemRepository.findAll(pageable);
    }
    
    public Page<Item> findByCategoryId(Long categoryId, Pageable pageable) {
        if (useJoinFetch) {
            return itemRepository.findByCategoryIdWithJoin(categoryId, pageable);
        }
        return itemRepository.findByCategoryId(categoryId, pageable);
    }
    
    public Item findById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    
    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }
}
