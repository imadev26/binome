package com.benchmark.jersey.repository;

import com.benchmark.jersey.entity.Item;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Item entity operations
 */
public class ItemRepository {
    
    @Inject
    private EntityManagerFactory emf;
    
    // Environment variable to control JOIN FETCH behavior
    private final boolean useJoinFetch = 
        Boolean.parseBoolean(System.getenv().getOrDefault("USE_JOIN_FETCH", "true"));
    
    /**
     * Find all items with pagination
     */
    public List<Item> findAll(int page, int size) {
        EntityManager em = emf.createEntityManager();
        try {
            String query = useJoinFetch 
                ? "SELECT i FROM Item i JOIN FETCH i.category ORDER BY i.id"
                : "SELECT i FROM Item i ORDER BY i.id";
                
            return em.createQuery(query, Item.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Count total items
     */
    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(i) FROM Item i", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Find item by ID
     */
    public Optional<Item> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            String query = useJoinFetch
                ? "SELECT i FROM Item i JOIN FETCH i.category WHERE i.id = :id"
                : "SELECT i FROM Item i WHERE i.id = :id";
                
            List<Item> results = em.createQuery(query, Item.class)
                    .setParameter("id", id)
                    .getResultList();
                    
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }
    
    /**
     * Find items by category ID with pagination
     */
    public List<Item> findByCategoryId(Long categoryId, int page, int size) {
        EntityManager em = emf.createEntityManager();
        try {
            String queryName = useJoinFetch 
                ? "Item.findByCategoryIdWithJoin" 
                : "Item.findByCategoryId";
                
            return em.createNamedQuery(queryName, Item.class)
                    .setParameter("categoryId", categoryId)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Count items by category ID
     */
    public long countByCategoryId(Long categoryId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(i) FROM Item i WHERE i.category.id = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Save (create or update) item
     */
    public Item save(Item item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Item result;
            if (item.getId() == null) {
                em.persist(item);
                result = item;
            } else {
                result = em.merge(item);
            }
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error saving item", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Delete item by ID
     */
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Item item = em.find(Item.class, id);
            if (item != null) {
                em.remove(item);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error deleting item", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Check if item exists
     */
    public boolean existsById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(i) FROM Item i WHERE i.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
