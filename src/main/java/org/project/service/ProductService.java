package org.project.service;

import org.project.entity.ProductEntity;
import org.project.enums.ProductSortType;
import org.project.enums.ProductType;
import org.project.model.response.PharmacyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for product operations, providing comprehensive product management
 * and search capabilities.
 */
public interface ProductService {
    
    // ==================== Search and filtering ====================
    
    /**
     * Search and filter products based on various criteria
     * @param searchQuery Optional text to search in product names
     * @param categoryId Optional category ID to filter by
     * @param minPrice Optional minimum price
     * @param maxPrice Optional maximum price
     * @param type Optional product type/tag to filter by
     * @param label Optional product label to filter by (NEW, SALE, etc.)
     * @param sortType Type of sorting to apply
     * @param pageable Pagination information
     * @return Page of products matching the search and filter criteria
     */
    Page<PharmacyResponse> searchProducts(Optional<String> searchQuery, 
                                          Optional<Long> categoryId,
                                          Optional<BigDecimal> minPrice, 
                                          Optional<BigDecimal> maxPrice,
                                          Optional<String> type,
                                          Optional<String> label,
                                          ProductSortType sortType, 
                                          Pageable pageable);
    
    // ==================== CRUD operations ====================
    
    /**
     * Save a product entity
     * @param product The product to save
     * @return The saved product entity
     */
    ProductEntity save(ProductEntity product);
    
    /**
     * Find a product by ID
     * @param id The product ID
     * @return The product entity if found, null otherwise
     */
    ProductEntity findById(Long id);
    
    /**
     * Find all products
     * @return List of all products
     */
    List<ProductEntity> findAll();
    
    /**
     * Delete a product by ID
     * @param id The product ID to delete
     */
    void deleteById(Long id);
    
    // ==================== Product display and fetching ====================
    
    /**
     * Find the top 10 products by rating
     * @return List of top 10 products
     */
    List<PharmacyResponse> findTop10Products();
    
    /**
     * Find a product by ID and return it as DTO
     * @param id The product ID
     * @return Product DTO if found, null otherwise
     */
    PharmacyResponse findProductById(Long id);
    
    /**
     * Find related products for a given product
     * @param productId The product ID to find related products for
     * @param limit Maximum number of related products to return
     * @return List of related products
     */
    List<PharmacyResponse> findRelatedProducts(Long productId, int limit);
    
    /**
     * Find the ID of the first product
     * @return The ID of the first product
     */
    Long findFirstProductId();
    
    // ==================== Type-specific operations ====================
    
    /**
     * Find all products by product type
     * @param productType The product type to filter by
     * @return List of products of the specified type
     */
    List<ProductEntity> findAllByProductType(ProductType productType);
    
    // ==================== Stock management ====================
    
    /**
     * Update the stock quantity of a product
     * @param id The product ID
     * @param quantity The new stock quantity
     */
    void updateStock(Long id, Integer quantity);

    void deleteProduct(Long id);
}