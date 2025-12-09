package com.benchmark.springdata.repository;

import com.benchmark.springdata.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Spring Data REST Repository for Item
 * Automatically exposes REST endpoints with search!
 */
@RepositoryRestResource(path = "items", collectionResourceRel = "items")
@CrossOrigin
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Custom search endpoint for filtering by category
     * Accessible at: GET /items/search/findByCategoryId?categoryId=1&page=0&size=50
     */
    @RestResource(path = "findByCategoryId", rel = "findByCategoryId")
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * With JOIN FETCH to avoid N+1
     */
    @RestResource(exported = false) // Not exposed as REST endpoint
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryIdWithJoin(@Param("categoryId") Long categoryId, Pageable pageable);

    // Spring Data REST automatically provides:
    // GET /items
    // GET /items/{id}
    // GET /items/{id}/category (relational link)
    // GET /items/search (discovers custom searches)
    // POST /items
    // PUT /items/{id}
    // PATCH /items/{id}
    // DELETE /items/{id}
}
