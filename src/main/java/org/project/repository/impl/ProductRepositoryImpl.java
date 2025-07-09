package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Custom implementation class for extended product repository functionality.
 * This class can be extended to provide custom query methods that cannot be
 * implemented using Spring Data JPA's method naming conventions.
 * 
 * After merging PharmacyRepository and ProductRepository, this class serves as a
 * central point for any custom repository methods needed for product operations.
 */
public class ProductRepositoryImpl {
    /**
     * JPA EntityManager for executing custom queries
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Example of how to implement a custom repository method:
     * 
     * <pre>
     * public List<ProductEntity> findProductsByCustomCriteria(String criteria) {
     *     String jpql = "SELECT p FROM ProductEntity p WHERE ...";
     *     return entityManager.createQuery(jpql, ProductEntity.class)
     *                       .setParameter("criteria", criteria)
     *                       .getResultList();
     * }
     * </pre>
     */
}
