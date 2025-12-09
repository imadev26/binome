package com.benchmark.springdata.repository;

import com.benchmark.springdata.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Spring Data REST Repository for Category
 * Automatically exposes REST endpoints!
 */
@RepositoryRestResource(path = "categories", collectionResourceRel = "categories")
@CrossOrigin
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // That's it! Spring Data REST auto-generates all CRUD endpoints:
    // GET    /categories
    // GET    /categories/{id}
    // POST   /categories
    // PUT    /categories/{id}
    // PATCH  /categories/{id}
    // DELETE /categories/{id}
}
