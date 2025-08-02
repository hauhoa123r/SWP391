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

public interface ProductService {
    
    // ==================== Search and filtering ====================
    
    Page<PharmacyResponse> searchProducts(Optional<String> searchQuery, 
                                          Optional<Long> categoryId,
                                          Optional<BigDecimal> minPrice, 
                                          Optional<BigDecimal> maxPrice,
                                          Optional<String> type,
                                          Optional<String> label,
                                          ProductSortType sortType, 
                                          Pageable pageable);
    
    // ==================== CRUD operations ====================
    
    ProductEntity save(ProductEntity product);
    
    ProductEntity findById(Long id);
    
    List<ProductEntity> findAll();
    
    void deleteById(Long id);
    
    // ==================== Product display and fetching ====================
    
    List<PharmacyResponse> findTop10Products();
    
    PharmacyResponse findProductById(Long id);
    
    List<PharmacyResponse> findRelatedProducts(Long productId, int limit);
    
    Long findFirstProductId();
    
    // ==================== Type-specific operations ====================
    
    List<ProductEntity> findAllByProductType(ProductType productType);
    
    // ==================== Stock management ====================
    
    void updateStock(Long id, Integer quantity);

    void deleteProduct(Long id);
}