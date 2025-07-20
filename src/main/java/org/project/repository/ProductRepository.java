package org.project.repository;

import jakarta.persistence.EntityManager;
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for product operations, providing both standard JPA methods
 * and custom queries for product data access. Uses Spring Data JPA method naming
 * conventions instead of explicit JPQL or native queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    
    /**
     * Get the EntityManager for custom queries
     * @return EntityManager instance
     */
    @Query("SELECT 1")
    default EntityManager getEntityManager() {
        throw new UnsupportedOperationException("This method should be overridden by Spring Data JPA");
    }
    
    // ==================== Basic product operations ====================
    
    /**
     * Find product by ID with active status
     * @param id Product ID
     * @return Optional containing the product if found with active status
     */
    Optional<ProductEntity> findByProductStatusAndId(ProductStatus productStatus, Long id);
    
    /**
     * Find all products with the specified product status
     * @param status Product status to filter by
     * @param pageable Pagination information
     * @return Page of products with the specified status
     */
    Page<ProductEntity> findByProductStatus(ProductStatus status, Pageable pageable);
    
    /**
     * Find the ID of the first active product
     * @return The ID of the first active product
     */
    Long findFirstByProductStatusOrderById(ProductStatus productStatus);
    
    // ==================== Product filtering by type ====================
    
    /**
     * Find all products by product type and status
     * @param productType Type of product
     * @param productStatus Status of product
     * @return List of products matching the type and status
     */
    List<ProductEntity> findAllByProductTypeAndProductStatus(ProductType productType, ProductStatus productStatus);
    
    /**
     * Find all products by status and type
     * @param productStatus Status of product
     * @param productType Type of product
     * @return List of products with specified status and type
     */
    List<ProductEntity> findByProductStatusAndProductType(ProductStatus productStatus, ProductType productType);
    
    /**
     * Find a product by product type and ID
     * @param productType Type of product
     * @param id Product ID
     * @return Product entity if found
     */
    ProductEntity findByProductTypeAndId(ProductType productType, Long id);
    
    /**
     * Check if a product with the specified type and ID exists
     * @param productType Type of product
     * @param id Product ID
     * @return true if the product exists, false otherwise
     */
    boolean existsByProductTypeAndId(ProductType productType, Long id);
    
    /**
     * Find products by product type with pagination
     * @param productType Type of product
     * @param pageable Pagination information
     * @return Page of products with the specified type
     */
    Page<ProductEntity> findByProductType(ProductType productType, Pageable pageable);
    
    /**
     * Find products by product type and name (case insensitive)
     * @param productType Type of product
     * @param name Name to search for
     * @param pageable Pagination information
     * @return Page of products matching the type and name
     */
    Page<ProductEntity> findByProductTypeAndNameContainingIgnoreCase(
            ProductType productType, String name, Pageable pageable);
    
    // ==================== Stock management ====================
    
    /**
     * Find products with stock quantities less than or equal to the threshold
     * @param threshold Stock quantity threshold
     * @return List of products with low stock
     */
    List<ProductEntity> findByStockQuantitiesLessThanEqual(Integer threshold);
    
    // ==================== Search operations ====================
    
    /**
     * Find products by name containing the specified text (case insensitive)
     * @param name Text to search for in product names
     * @param pageable Pagination information
     * @return Page of products matching the search criteria
     */
    Page<ProductEntity> findByProductStatusAndNameContainingIgnoreCase(ProductStatus productStatus, String name, Pageable pageable);
    
    /**
     * Find products by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param pageable Pagination information
     * @return Page of products with prices in the specified range
     */
    Page<ProductEntity> findByProductStatusAndPriceBetween(ProductStatus productStatus, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // ==================== Category and label filtering ====================
    
    /**
     * Find products by category ID
     * @param productStatus Product status to filter by
     * @param categoryId Category ID to filter by
     * @param pageable Pagination information
     * @return Page of products belonging to the specified category
     */
    Page<ProductEntity> findByProductStatusAndCategoryEntities_Id(ProductStatus productStatus, Long categoryId, Pageable pageable);
    
    /**
     * Find products by label
     * @param productStatus Product status to filter by
     * @param label Product label to filter by
     * @param pageable Pagination information
     * @return Page of products with the specified label
     */
    Page<ProductEntity> findByProductStatusAndLabel(ProductStatus productStatus, Label label, Pageable pageable);
    
    // ==================== Tag operations ====================
    
    /**
     * Find products by tag name (case insensitive)
     * @param productStatus Product status to filter by
     * @param tagName Tag name to filter by (case insensitive)
     * @param pageable Pagination information
     * @return Page of products with the specified tag
     */
    Page<ProductEntity> findByProductStatusAndProductTagEntities_Id_NameIgnoreCase(ProductStatus productStatus, String tagName, Pageable pageable);
    
    // ==================== Special queries ====================
    
    /**
     * Find all active products with eager loading of categories
     * @param productStatus Product status to filter by
     * @return List of products with the specified status
     */
    List<ProductEntity> findDistinctByProductStatus(ProductStatus productStatus);

    List<ProductEntity> findByProductType(String string);
}
