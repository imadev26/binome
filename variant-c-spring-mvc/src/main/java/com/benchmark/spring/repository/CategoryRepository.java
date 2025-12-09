package com.benchmark.spring.repository;

import com.benchmark.spring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring Data JPA provides all basic CRUD operations automatically
    // findAll, findById, save, delete, etc.
}
