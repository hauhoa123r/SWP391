package org.project.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductSortType;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.LogicalOperator;
import org.project.model.response.PharmacyResponse;
import org.project.repository.ProductRepository;
import org.project.service.ProductService;
import org.project.utils.specification.SpecificationUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * Modern, Specification-based implementation of ProductService interface
 * that provides comprehensive product management and search capabilities.
 */
@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ConverterPharmacyProduct converter;
    private final ObjectProvider<SpecificationUtils<ProductEntity>> specUtilsProvider;
    
    @PersistenceContext
    private EntityManager entityManager;

    public ProductServiceImpl(ProductRepository productRepository,
                              ConverterPharmacyProduct converter,
                              ObjectProvider<SpecificationUtils<ProductEntity>> specUtilsProvider) {
        this.productRepository = productRepository;
        this.converter = converter;
        this.specUtilsProvider = specUtilsProvider;
    }

    // ==================== Search & Filter ====================

    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyResponse> searchProducts(Optional<String> searchQuery,
                                                 Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice,
                                                 Optional<BigDecimal> maxPrice,
                                                 Optional<String> type,
                                                 ProductSortType sortType,
                                                 Pageable pageable) {
        try {
            log.info("Starting optimized product search");
            
            // Build a single query with all filters
            String jpql = "SELECT p FROM ProductEntity p WHERE p.productStatus = :status " +
                          "AND (p.productType = :medicineType OR p.productType = :medicalProductType)";
            
            // Add search query filter if present
            if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                jpql += " AND LOWER(p.name) LIKE :searchQuery";
            }
            
            // Add category filter if present
            if (categoryId.isPresent()) {
                jpql += " AND EXISTS (SELECT c FROM p.categoryEntities c WHERE c.id = :categoryId)";
            }
            
            // Add price range filter if present
            if (minPrice.isPresent() || maxPrice.isPresent()) {
                if (minPrice.isPresent()) {
                    jpql += " AND p.price >= :minPrice";
                }
                if (maxPrice.isPresent()) {
                    jpql += " AND p.price <= :maxPrice";
                }
            }
            
            // Add tag filter if present
            if (type.isPresent() && !type.get().isEmpty()) {
                jpql += " AND EXISTS (SELECT t FROM p.productTagEntities t WHERE LOWER(t.id.name) = LOWER(:tagName))";
            }
            
            // Create the query
            TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
            
            // Set parameters
            query.setParameter("status", ProductStatus.ACTIVE);
            query.setParameter("medicineType", ProductType.MEDICINE);
            query.setParameter("medicalProductType", ProductType.MEDICAL_PRODUCT);
            
            if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                query.setParameter("searchQuery", "%" + searchQuery.get().toLowerCase() + "%");
            }
            
            if (categoryId.isPresent()) {
                query.setParameter("categoryId", categoryId.get());
            }
            
            if (minPrice.isPresent()) {
                query.setParameter("minPrice", minPrice.get());
            }
            
            if (maxPrice.isPresent()) {
                query.setParameter("maxPrice", maxPrice.get());
            }
            
            if (type.isPresent() && !type.get().isEmpty()) {
                query.setParameter("tagName", type.get());
            }
            
            // Count total results
            String countJpql = jpql.replace("SELECT p", "SELECT COUNT(p)");
            TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
            
            // Set the same parameters for count query
            countQuery.setParameter("status", ProductStatus.ACTIVE);
            countQuery.setParameter("medicineType", ProductType.MEDICINE);
            countQuery.setParameter("medicalProductType", ProductType.MEDICAL_PRODUCT);
            
            if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                countQuery.setParameter("searchQuery", "%" + searchQuery.get().toLowerCase() + "%");
            }
            
            if (categoryId.isPresent()) {
                countQuery.setParameter("categoryId", categoryId.get());
            }
            
            if (minPrice.isPresent()) {
                countQuery.setParameter("minPrice", minPrice.get());
            }
            
            if (maxPrice.isPresent()) {
                countQuery.setParameter("maxPrice", maxPrice.get());
            }
            
            if (type.isPresent() && !type.get().isEmpty()) {
                countQuery.setParameter("tagName", type.get());
            }
            
            Long totalCount = countQuery.getSingleResult();
            
            // Apply sorting
            if (sortType == ProductSortType.PRICE_ASC) {
                jpql += " ORDER BY p.price ASC";
            } else if (sortType == ProductSortType.PRICE_DESC) {
                jpql += " ORDER BY p.price DESC";
            }
            
            // Create the final query with sorting
            if (sortType == ProductSortType.PRICE_ASC || sortType == ProductSortType.PRICE_DESC) {
                query = entityManager.createQuery(jpql, ProductEntity.class);
                
                // Set parameters again
                query.setParameter("status", ProductStatus.ACTIVE);
                query.setParameter("medicineType", ProductType.MEDICINE);
                query.setParameter("medicalProductType", ProductType.MEDICAL_PRODUCT);
                
                if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                    query.setParameter("searchQuery", "%" + searchQuery.get().toLowerCase() + "%");
                }
                
                if (categoryId.isPresent()) {
                    query.setParameter("categoryId", categoryId.get());
                }
                
                if (minPrice.isPresent()) {
                    query.setParameter("minPrice", minPrice.get());
                }
                
                if (maxPrice.isPresent()) {
                    query.setParameter("maxPrice", maxPrice.get());
                }
                
                if (type.isPresent() && !type.get().isEmpty()) {
                    query.setParameter("tagName", type.get());
                }
            }
            
            // Apply pagination
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            
            // Execute query
            List<ProductEntity> results = query.getResultList();
            log.info("Found {} products with optimized query", results.size());
            
            // Special handling for rating sort
            if (sortType == ProductSortType.RATING) {
                results.sort(Comparator.comparing(this::calculateRating).reversed());
            }

            // Convert to DTOs
            List<PharmacyResponse> dtos = results.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            // Create page
            return new PageImpl<>(dtos, pageable, totalCount);
        } catch (Exception e) {
            log.error("Error in optimized product search: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search products", e);
        }
    }

    // ==================== CRUD Operations ====================

    @Override
    @Transactional
    public ProductEntity save(ProductEntity product) {
        try {
            log.debug("Saving product: {}", product.getName());
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductEntity findById(Long id) {
        try {
            log.debug("Finding product by ID: {}", id);
            return productRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error finding product by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        try {
            log.debug("Finding all products");
            
            // Trực tiếp lấy các sản phẩm với type là MEDICINE hoặc MEDICAL_PRODUCT
            List<ProductEntity> medicineProducts = productRepository.findAllByProductTypeAndProductStatus(
                    ProductType.MEDICINE, ProductStatus.ACTIVE);
            List<ProductEntity> medicalProducts = productRepository.findAllByProductTypeAndProductStatus(
                    ProductType.MEDICAL_PRODUCT, ProductStatus.ACTIVE);
            
            // Gộp hai danh sách
            List<ProductEntity> allFilteredProducts = new ArrayList<>();
            allFilteredProducts.addAll(medicineProducts);
            allFilteredProducts.addAll(medicalProducts);
            
            log.info("Found {} MEDICINE products and {} MEDICAL_PRODUCT products for findAll", 
                    medicineProducts.size(), medicalProducts.size());
                    
            return allFilteredProducts;
        } catch (Exception e) {
            log.error("Error finding all products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            log.debug("Deleting product by ID: {}", id);
            productRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting product by ID: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    // ==================== Product Display & Fetching ====================

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findTop10Products() {
        try {
            log.debug("Finding top 10 products");
            
            // Trực tiếp lấy các sản phẩm với type là MEDICINE hoặc MEDICAL_PRODUCT
            List<ProductEntity> medicineProducts = productRepository.findAllByProductTypeAndProductStatus(
                    ProductType.MEDICINE, ProductStatus.ACTIVE);
            List<ProductEntity> medicalProducts = productRepository.findAllByProductTypeAndProductStatus(
                    ProductType.MEDICAL_PRODUCT, ProductStatus.ACTIVE);
            
            // Gộp hai danh sách
            List<ProductEntity> allFilteredProducts = new ArrayList<>();
            allFilteredProducts.addAll(medicineProducts);
            allFilteredProducts.addAll(medicalProducts);
            
            return allFilteredProducts.stream()
                    .sorted(Comparator.comparingDouble(this::calculateRating).reversed())
                    .limit(10)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding top 10 products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyResponse findProductById(Long id) {
        try {
            log.debug("Finding product DTO by ID: {}", id);
            ProductEntity product = productRepository.findByProductStatusAndId(ProductStatus.ACTIVE, id).orElse(null);
            
            if (product != null && (product.getProductType() == ProductType.MEDICINE || 
                                   product.getProductType() == ProductType.MEDICAL_PRODUCT)) {
                return convertToDto(product);
            }
            return null;
        } catch (Exception e) {
            log.error("Error finding product DTO by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findRelatedProducts(Long productId, int limit) {
        try {
            log.debug("Finding {} related products for product ID: {}", limit, productId);
            ProductEntity base = productRepository.findById(productId).orElse(null);
            if (base == null || base.getCategoryEntities() == null || base.getCategoryEntities().isEmpty()) {
                return List.of();
            }

            Long categoryId = base.getCategoryEntities().iterator().next().getId();
            
            // Lấy tất cả sản phẩm trong cùng category
            List<ProductEntity> categoryProducts = productRepository
                    .findByProductStatusAndCategoryEntities_Id(ProductStatus.ACTIVE, categoryId,
                            PageRequest.of(0, limit * 2)).getContent();

            // Lọc theo type
            List<ProductEntity> related = categoryProducts.stream()
                    .filter(p -> p.getProductType() == ProductType.MEDICINE || 
                                p.getProductType() == ProductType.MEDICAL_PRODUCT)
                    .filter(p -> !p.getId().equals(productId))
                    .collect(Collectors.toList());

            return related.stream()
                    .sorted(Comparator.comparingDouble(this::calculateRating).reversed())
                    .limit(limit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding related products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long findFirstProductId() {
        try {
            log.debug("Finding first product ID");
            return productRepository.findFirstByProductStatusOrderById(ProductStatus.ACTIVE);
        } catch (Exception e) {
            log.error("Error finding first product ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAllByProductType(ProductType productType) {
        try {
            log.debug("Finding all products by type: {}", productType);
            return productRepository.findAllByProductTypeAndProductStatus(productType, ProductStatus.ACTIVE)
                    .stream()
                    .filter(p -> !belongsToType(p) && isAllowedType(p))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding products by type: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // ==================== Stock Management ====================

    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        try {
            log.debug("Updating stock for product ID: {} to quantity: {}", id, quantity);
            productRepository.findById(id).ifPresentOrElse(
                    product -> {
                        product.setStockQuantities(quantity);
                        productRepository.save(product);
                    },
                    () -> log.warn("Cannot update stock: product with ID {} not found", id)
            );
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update product stock", e);
        }
    }

    // ==================== Internal Helper Methods ====================

    private boolean belongsToType(ProductEntity entity) {
        // Trả về false để không lọc bỏ bất kỳ sản phẩm nào
            return false;
    }

    private boolean isAllowedType(ProductEntity entity) {
        // Log product type for debugging
        if (entity == null) {
            log.warn("Entity is null when checking allowed type");
            return false;
        }
        
        ProductType type = entity.getProductType();
        log.info("Checking product type for entity ID {}: {}", entity.getId(), type);
        
        if (type == null) {
            log.warn("Product type is null for product ID: {}", entity.getId());
            // Allow products with null type for now for debugging
            return true;
        }
        
        // Temporarily allow all products for debugging
        return true;
    }

    private PharmacyResponse convertToDto(ProductEntity entity) {
        return converter.toDto(entity);
    }

    private Specification<ProductEntity> buildSpecification(Optional<String> searchQuery,
                                                            Optional<Long> categoryId,
                                                            Optional<BigDecimal> minPrice,
                                                            Optional<BigDecimal> maxPrice,
                                                            Optional<String> type) {
        SpecificationUtils<ProductEntity> util = specUtilsProvider.getObject().reset();

        // Add search query criteria
        searchQuery.ifPresent(q -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("name")
                        .comparisonOperator(ComparisonOperator.CONTAINS)
                        .comparedValue(q)
                        .joinType(JoinType.INNER)
                        .build(), LogicalOperator.AND));

        // Exclude category TEST
        util.addSearchCriteria(SearchCriteria.builder()
                .fieldName("categoryEntities.name")
                .comparisonOperator(ComparisonOperator.NOT_EQUALS)
                .comparedValue("TEST")
                .joinType(JoinType.INNER)
                .build(), LogicalOperator.AND);

        // Add category criteria
        categoryId.ifPresent(id -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("categoryEntities.id")
                        .comparisonOperator(ComparisonOperator.EQUALS)
                        .comparedValue(id)
                        .joinType(JoinType.INNER)
                        .build(), LogicalOperator.AND));

        // Add price range criteria
        if (minPrice.isPresent() || maxPrice.isPresent()) {
            BigDecimal min = minPrice.orElse(BigDecimal.ZERO);
            BigDecimal max = maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE));
            util.addSearchCriteria(SearchCriteria.builder()
                    .fieldName("price")
                    .comparisonOperator(ComparisonOperator.BETWEEN)
                    .comparedValue(new BigDecimal[]{min, max})
                    .joinType(JoinType.INNER)
                    .build(), LogicalOperator.AND);
        }

        // Add type criteria - Don't use join on label as it's a basic field
        type.filter(t -> !t.isBlank()).ifPresent(t -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("label")
                        .comparisonOperator(ComparisonOperator.EQUALS)
                        .comparedValue(t.trim().toUpperCase())
                        // Remove joinType for non-entity field
                        .build(), LogicalOperator.AND));

        return util.getSpecification();
    }

    private Page<PharmacyResponse> applyRatingSortWithPagination(Specification<ProductEntity> spec, Pageable pageable) {
        List<PharmacyResponse> sorted = productRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(PharmacyResponse::getRating,
                        Comparator.nullsLast(Double::compareTo)).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<PharmacyResponse> subList = start >= sorted.size() ? List.of() : sorted.subList(start, end);
        return new PageImpl<>(subList, pageable, sorted.size());
    }

    private Pageable createSortedPageable(Pageable pageable, ProductSortType sortType) {
        if (sortType == null || sortType == ProductSortType.DEFAULT || sortType == ProductSortType.RATING) {
            return pageable;
        }
        Sort sort = switch (sortType) {
            case PRICE_ASC -> Sort.by("price").ascending();
            case PRICE_DESC -> Sort.by("price").descending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private double calculateRating(ProductEntity entity) {
        if (entity == null || entity.getReviewEntities() == null || entity.getReviewEntities().isEmpty()) {
            return 0.0;
        }
        return entity.getReviewEntities().stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating())
                .average()
                .orElse(0.0);
    }

    private String sanitizeSearchQuery(Optional<String> queryOpt) {
        if (queryOpt == null || queryOpt.isEmpty()) {
            return null;
        }

        String query = queryOpt.get().trim();
        if (query.isEmpty()) {
            return null;
        }

        // Remove diacritical marks
        query = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{L}\\p{Nd}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return query.isBlank() ? null : query.toLowerCase();
    }

    private void validateInputs(Pageable pageable, Optional<BigDecimal> minPrice,
                                Optional<BigDecimal> maxPrice, Optional<Long> categoryId) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        if (pageable.getPageSize() <= 0 || pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Invalid pageable parameters");
        }

        if (minPrice.isPresent() && maxPrice.isPresent() && minPrice.get().compareTo(maxPrice.get()) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        }

        if (categoryId.isPresent() && categoryId.get() <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
    }
}