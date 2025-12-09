package com.benchmark.jersey.repository;

import com.benchmark.jersey.entity.Category;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity operations
 */
public class CategoryRepository {
    
    @Inject
    private EntityManagerFactory emf;
    
    /**
     * Find all categories with pagination
     */
    public List<Category> findAll(int page, int size) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.id", Category.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Count total categories
     */
    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Find category by ID
     */
    public Optional<Category> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Category category = em.find(Category.class, id);
            return Optional.ofNullable(category);
        } finally {
            em.close();
        }
    }
    
    /**
     * Find category by ID with items (eager fetch)
     */
    public Optional<Category> findByIdWithItems(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Category> results = em.createQuery(
                "SELECT c FROM Category c LEFT JOIN FETCH c.items WHERE c.id = :id", 
                Category.class)
                .setParameter("id", id)
                .getResultList();
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }
    
    /**
     * Save (create or update) category
     */
    public Category save(Category category) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category result;
            if (category.getId() == null) {
                em.persist(category);
                result = category;
            } else {
                result = em.merge(category);
            }
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error saving category", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Delete category by ID
     */
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error deleting category", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Check if category exists
     */
    public boolean existsById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
