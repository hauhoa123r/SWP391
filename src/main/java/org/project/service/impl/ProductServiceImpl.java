package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final PharmacyRepository pharmacyRepository;
    private final ConverterPharmacyProduct converter;

    @Override
    public Page<PharmacyResponse> searchProducts(Optional<String> searchQuery, Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                 Optional<String> type, ProductSortType sortType, Pageable pageable) {
        String sanitizedQuery = sanitizeSearchQuery(searchQuery);
        Optional<String> processedSearchQuery = sanitizedQuery != null ? Optional.of(sanitizedQuery) : Optional.empty();

        validateCommonInputs(pageable, minPrice, maxPrice, processedSearchQuery, type, categoryId);

        log.info("Searching products: searchQuery={}, categoryId={}, minPrice={}, maxPrice={}, type={}, sortType={}",
                processedSearchQuery.orElse(""), categoryId.orElse(null), minPrice.orElse(null), maxPrice.orElse(null), type.orElse(""), sortType);

        try {
            Pageable sortedPageable = createSortedPageable(pageable, sortType);
            Page<ProductEntity> productPage = determineSearchStrategy(processedSearchQuery, categoryId, minPrice, maxPrice, type, sortedPageable);
            Page<PharmacyResponse> resultPage = productPage.map(this::convertToDto);

            if (sortType == ProductSortType.RATING) {
                List<ProductEntity> allProducts = fetchAllProducts(processedSearchQuery, categoryId, minPrice, maxPrice, type);
                if (allProducts.isEmpty()) {
                    return Page.empty(pageable);
                }
                List<PharmacyResponse> allDtos = allProducts.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                List<PharmacyResponse> sortedContent = sortByRating(allDtos);
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), sortedContent.size());
                List<PharmacyResponse> paginatedContent = sortedContent.subList(start, end);
                return new PageImpl<>(paginatedContent, pageable, sortedContent.size());
            }

            return applySorting(resultPage, sortType);
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage());
            throw new RuntimeException("Failed to search products", e);
        }
    }

    private String sanitizeSearchQuery(Optional<String> searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return null;
        }

        // Step 1: trim and quick blank check
        String query = searchQuery.get().trim();
        if (query.isEmpty()) {
            return null;
        }

        // Bỏ dấu
        query = Normalizer.normalize(query, Normalizer.Form.NFD);
        query = query.replaceAll("\\p{M}", "");

        // Giữ chữ, số và space
        query = query.replaceAll("[^\\p{L}\\p{Nd}\\s]", " ");

        // Gom khoảng trắng và trim
        query = query.replaceAll("\\s+", " ").trim();

        return query.isEmpty() ? null : query.toLowerCase();
    }


    Pageable createSortedPageable(Pageable pageable, ProductSortType sortType) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
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

    private Page<ProductEntity> determineSearchStrategy(Optional<String> searchQuery, Optional<Long> categoryId,
                                                        Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                        Optional<String> type, Pageable pageable) {
        try {
            if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                return searchByName(searchQuery.get(), pageable);
            }
            if (categoryId.isPresent()) {
                return searchByCategory(categoryId.get(), pageable);
            }
            if (minPrice.isPresent() || maxPrice.isPresent()) {
                return searchByPriceRange(minPrice.orElse(BigDecimal.ZERO), maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE)), pageable);
            }
            if (type.isPresent() && !type.get().trim().isEmpty()) {
                return searchByType(type.get().trim(), pageable);
            }
            return pharmacyRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Error determining search strategy: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    Page<ProductEntity> searchByName(String name, Pageable pageable) {
        validateCommonInputs(pageable, null, null, Optional.of(name), Optional.empty(), Optional.empty());
        try {
            // Sử dụng logic Java để tìm kiếm tập hợp ký tự
            Page<ProductEntity> allProducts = pharmacyRepository.findAll(pageable);
            List<ProductEntity> matchedProducts = allProducts.getContent().stream()
                    .filter(product -> matchesCharacterSet(name, product.getName()))
                    .collect(Collectors.toList());
            return new PageImpl<>(matchedProducts, pageable, matchedProducts.size());
        } catch (Exception e) {
            log.error("Error searching by name: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    private boolean matchesCharacterSet(String query, String productName) {
        if (query == null || productName == null) {
            return false;
        }
        // Bỏ dấu và ký tự đặc biệt, đồng bộ với sanitizeSearchQuery
        String sanitizedQuery = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{L}\\p{Nd}]", "")
                .toLowerCase();
        String sanitizedName = Normalizer.normalize(productName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{L}\\p{Nd}]", "")
                .toLowerCase();

        if (sanitizedQuery.isEmpty() || sanitizedName.isEmpty()) {
            return false;
        }

        // 1. Ưu tiên khớp theo chuỗi con đúng thứ tự
        if (sanitizedName.contains(sanitizedQuery)) {
            return true; // 100% match
        }

        // 2. Tính % ký tự khớp theo thứ tự (longest common subsequence)
        int lcs = longestCommonSubsequence(sanitizedQuery, sanitizedName);
        double similarity = (double) lcs / sanitizedQuery.length();
        return similarity >= 0.5; // yêu cầu >= 50%
    }

    // Hỗ trợ tính LCS độ dài O(n*m) – chuỗi ở đây khá ngắn nên chấp nhận được
    private int longestCommonSubsequence(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[n][m];
    }

    private Page<ProductEntity> searchByCategory(Long categoryId, Pageable pageable) {
        validateCommonInputs(pageable, null, null, Optional.empty(), Optional.empty(), Optional.of(categoryId));
        try {
            return pharmacyRepository.findByCategoryEntities_Id(categoryId, pageable);
        } catch (Exception e) {
            log.error("Error searching by category: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    private Page<ProductEntity> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        validateCommonInputs(pageable, Optional.of(minPrice), Optional.of(maxPrice), Optional.empty(), Optional.empty(), Optional.empty());
        try {
            return pharmacyRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Error searching by price range: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    private Page<ProductEntity> searchByType(String type, Pageable pageable) {
        validateCommonInputs(pageable, null, null, Optional.empty(), Optional.of(type), Optional.empty());
        String value = type.trim();
        try {
            Page<ProductEntity> byTag = pharmacyRepository.findByTagName(value, pageable);
            if (!byTag.isEmpty()) {
                return byTag;
            }
            ProductLabel label = ProductLabel.valueOf(value.toUpperCase());
            return pharmacyRepository.findByLabel(label, pageable);
        } catch (IllegalArgumentException e) {
            log.warn("No ProductLabel found for value: {}", value);
            return Page.empty(pageable);
        } catch (Exception e) {
            log.error("Error searching by type: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    private List<ProductEntity> fetchAllProducts(Optional<String> searchQuery, Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                 Optional<String> type) {
        String sanitizedQuery = sanitizeSearchQuery(searchQuery);
        Optional<String> processedSearchQuery = sanitizedQuery != null ? Optional.of(sanitizedQuery) : Optional.empty();
        validateCommonInputs(null, minPrice, maxPrice, processedSearchQuery, type, categoryId);
        try {
            if (processedSearchQuery.isPresent() && !processedSearchQuery.get().isEmpty()) {
                return searchByName(processedSearchQuery.get(), Pageable.unpaged())
                        .getContent();
            }
            if (categoryId.isPresent()) {
                return pharmacyRepository.findByCategoryEntities_Id(categoryId.get(), Pageable.unpaged())
                        .getContent();
            }
            if (minPrice.isPresent() || maxPrice.isPresent()) {
                return pharmacyRepository.findByPriceBetween(minPrice.orElse(BigDecimal.ZERO),
                                maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE)), Pageable.unpaged())
                        .getContent();
            }
            if (type.isPresent() && !type.get().trim().isEmpty()) {
                String value = type.get().trim();
                List<ProductEntity> byTag = pharmacyRepository.findByTagName(value, Pageable.unpaged()).getContent();
                if (!byTag.isEmpty()) {
                    return byTag;
                }
                try {
                    ProductLabel label = ProductLabel.valueOf(value.toUpperCase());
                    return pharmacyRepository.findByLabel(label, Pageable.unpaged())
                            .getContent();
                } catch (IllegalArgumentException e) {
                    log.warn("No ProductLabel found for value: {}", value);
                    return List.of();
                }
            }
            return pharmacyRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage());
            return List.of();
        }
    }

    private Page<PharmacyResponse> applySorting(Page<PharmacyResponse> page, ProductSortType sortType) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }
        if (sortType == null || sortType == ProductSortType.DEFAULT) {
            return page;
        }
        if (page.getContent().isEmpty()) {
            return page;
        }
        List<PharmacyResponse> content = new ArrayList<>(page.getContent());
        switch (sortType) {
            case PRICE_ASC -> content.sort(Comparator.comparing(PharmacyResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
            case PRICE_DESC -> content.sort(Comparator.comparing(PharmacyResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            default -> {}
        }
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private PharmacyResponse convertToDto(ProductEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ProductEntity cannot be null");
        }
        if (converter == null) {
            throw new IllegalStateException("Converter is not initialized");
        }
        try {
            return converter.toDto(entity);
        } catch (Exception e) {
            log.error("Error converting ProductEntity to PharmacyResponse: {}", e.getMessage());
            throw new RuntimeException("Failed to convert product data", e);
        }
    }

    private double calculateRating(ProductEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ProductEntity cannot be null");
        }
        if (entity.getReviewEntities() == null || entity.getReviewEntities().isEmpty()) {
            return 0.0;
        }
        try {
            return entity.getReviewEntities().stream()
                    .filter(review -> review.getRating() != null)
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            log.error("Error calculating rating: {}", e.getMessage());
            return 0.0;
        }
    }

    private List<PharmacyResponse> sortByRating(List<PharmacyResponse> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        List<PharmacyResponse> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparing(PharmacyResponse::getRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return sortedProducts;
    }

    private void validateCommonInputs(Pageable pageable, Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                      Optional<String> searchQuery, Optional<String> type, Optional<Long> categoryId) {
        if (pageable != null) {
            if (pageable.getPageSize() <= 0 || pageable.getPageNumber() < 0) {
                throw new IllegalArgumentException("Invalid page size or page number");
            }
        }
        if (minPrice != null && minPrice.isPresent() && minPrice.get().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (maxPrice != null && maxPrice.isPresent() && maxPrice.get().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.isPresent() && maxPrice.isPresent()) {
            if (minPrice.get().compareTo(maxPrice.get()) > 0) {
                throw new IllegalArgumentException("Minimum price must be less than or equal to maximum price");
            }
        }
        if (searchQuery != null && searchQuery.isPresent() && searchQuery.get().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty after sanitization");
        }
        if (type != null && type.isPresent()) {
            String value = type.get().trim();
            // Allow blank value to be treated as not provided
            if (!value.isEmpty()) {
                if (!value.matches("^[a-zA-Z0-9]+$")) {
                    throw new IllegalArgumentException("Product type contains invalid characters");
                }
            }
        }
        if (categoryId != null && categoryId.isPresent() && categoryId.get() <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
    }
}