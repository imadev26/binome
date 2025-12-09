package com.benchmark.spring.repository;

import com.benchmark.spring.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Item
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find items by category ID with pagination
     * Uses JOIN FETCH if USE_JOIN_FETCH environment variable is true (default)
     */
    @Query("SELECT i FROM Item i WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * Find items by category ID with JOIN FETCH to avoid N+1
     */
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryIdWithJoin(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * Find all items with JOIN FETCH
     */
    @Query(value = "SELECT i FROM Item i JOIN FETCH i.category",
           countQuery = "SELECT COUNT(i) FROM Item i")
    Page<Item> findAllWithCategory(Pageable pageable);
}
